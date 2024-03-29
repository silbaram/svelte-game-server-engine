package com.github.silbaram.infrastructures.server.threadpool

import com.github.silbaram.infrastructures.server.configuration.NettyServerByKotlinTemplate
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@EnableAsync
@Configuration
class NettyServerThreadPool(
    private val nettyServerConfigs: Map<String, NettyServerByKotlinTemplate>
): AsyncConfigurer {

    val logger = LoggerFactory.getLogger(NettyServerThreadPool::class.java)
    private val executorService: ExecutorService = Executors.newFixedThreadPool(nettyServerConfigs.keys.size)

    override fun getAsyncExecutor(): Executor {
        return executorService
    }

    @PreDestroy
    fun shutdown() {
        executorService.shutdown()
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            logger.info("Executor did not terminate in the specified time.")
            val droppedTasks: List<Runnable> = executorService.shutdownNow()
            logger.info("Executor was abruptly shut down. ${droppedTasks.size} tasks will not be executed.")
        }
    }
}