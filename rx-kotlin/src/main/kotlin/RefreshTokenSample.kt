import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.atomic.AtomicBoolean

val isTokenExpired: AtomicBoolean = AtomicBoolean(true)

fun main() {
    getUser()
        .refreshTokenIfNeed()
        .subscribeBy(
            onNext = { result ->
                println("onNext: $result")
            },
            onError = { exception ->
                println("onError: ${exception.message}")
            }
        )
}

fun getUser(): Observable<String> = Observable.create { emitter ->
    if (isTokenExpired.get()) {
        println("process: getUser() but token is expired!")
        emitter.onError(Throwable("token_expired"))
    } else {
        println("process: getUser()")
        emitter.onNext("getUser() success!")
    }
}

fun refreshToken(): Observable<String> = Observable.create { emitter ->
    println("process: refreshToken()")
    isTokenExpired.set(false)
    emitter.onNext("refreshToken()")
}

fun <T : Any> Observable<T>.refreshTokenIfNeed(): Observable<T> = retryWhen { attempt ->
    attempt.flatMap { exception ->
        println("refreshTokenIfNeed: $exception")
        if (exception.message == "token_expired") {
            refreshToken()
        } else {
            Observable.error(exception)
        }
    }
}
