package org.service.toyhelloworld.adapter.out.persistent.repository

import jakarta.transaction.Transactional
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaWalletEntity
import org.service.toyhelloworld.adapter.out.persistent.entity.JpaWalletMapper
import org.service.toyhelloworld.adapter.out.persistent.exception.RetryExhaustedWithOptimisticLockingFailureException
import org.service.toyhelloworld.domain.wallet.Wallet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Repository
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigDecimal

@Repository
class JpaWalletRepository(
    private val springDataJpaWalletRepository: SpringDataJpaWalletRepository,
    private val jpaWalletMapper: JpaWalletMapper,
    private val walletTransactionRepository: WalletTransactionRepository,
    private val transactionTemplate: TransactionTemplate,
) : WalletRepository {

    override fun getWallets(sellerIds: Set<Long>): Set<Wallet> {
        return springDataJpaWalletRepository.findByUserIdIn(sellerIds)
            .map { jpaWalletMapper.mapToDomainEntity(it) }
            .toSet()
    }
    override fun save(wallets: List<Wallet>) {
       try {
           performSaveOperation(wallets)
       } catch (e : ObjectOptimisticLockingFailureException){
           retrySaveOperation(wallets)
       }
    }


    private fun performSaveOperation(wallets: List<Wallet>) {

        transactionTemplate.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW

        transactionTemplate.execute {
            springDataJpaWalletRepository.saveAll(wallets.map { jpaWalletMapper.mapToJpaEntity(it) })
            walletTransactionRepository.save(wallets.flatMap { it.walletTransactions })
        }
    }

    private fun retrySaveOperation(wallets: List<Wallet>, maxRetries: Int = 3, baseDelay: Int = 100) {
        var retryCount = 0

        while (true) {
            try {
                performSaveOperationWithRecent(wallets)
                break;
            } catch (e : ObjectOptimisticLockingFailureException){
                if(++retryCount > maxRetries){
                    throw RetryExhaustedWithOptimisticLockingFailureException(e.message?: "exhausted retry count")
                }
            }
            waitForNextRetry(baseDelay)
        }
    }

    private fun performSaveOperationWithRecent(wallets: List<Wallet>) {
        val recentWallets = springDataJpaWalletRepository.findByIdIn(wallets.map { it.id }.toSet())
        val recentWalletsById = recentWallets.associateBy { it.id }

        val walletPairs = wallets.map { wallet -> Pair(wallet, recentWalletsById[wallet.id]!!) }

        val updatedWallet = walletPairs.map {
            it.second.addBalance(BigDecimal(it.first.walletTransactions.sumOf { it.amount }))
        }

        transactionTemplate.execute{
            springDataJpaWalletRepository.saveAll(updatedWallet)
            walletTransactionRepository.save(wallets.flatMap { it.walletTransactions })
        }
    }

    private fun waitForNextRetry(baseDelay: Int) {
        val jitter = (Math.random() * baseDelay).toLong()
        try {
            Thread.sleep(jitter)
        } catch (e : InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("Interrupted while waiting for $baseDelay minutes", e)
        }
    }
}

interface SpringDataJpaWalletRepository : JpaRepository<JpaWalletEntity, Long> {
    fun findByUserIdIn(userIds: Set<Long>): List<JpaWalletEntity>
    fun findByIdIn(ids: Set<Long>): List<JpaWalletEntity>
}