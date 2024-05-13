package cc.seaotter.tomatoes.data.service

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}