import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.atomic.AtomicBoolean

val isTokenExpiredGraphQL: AtomicBoolean = AtomicBoolean(true)

fun main() {
    getUserGraphQL()
        .handleErrorGraphQL()
        .refreshTokenIfNeedGraphQL()
        .handleError()
        .subscribeBy(
            onNext = { result ->
                println("onNext: $result")
            },
            onError = { exception ->
                println("onError: ${exception.message}")
            }
        )
}

fun getUserGraphQL(): Observable<String> = Observable.create { emitter ->
    if (isTokenExpiredGraphQL.get()) {
        println("process: getUser() but token is expired!")
//        emitter.onNext("token_expired")
        emitter.onNext("bad_request")
    } else {
        println("process: getUser()")
        emitter.onNext("getUser() success!")
    }
}

fun refreshTokenGraphQL(): Observable<String> = Observable.create { emitter ->
    println("process: refreshToken()")
    isTokenExpiredGraphQL.set(false)
    emitter.onNext("refreshToken()")
}

fun <T : Any> Observable<T>.handleErrorGraphQL(): Observable<T> = doOnNext { response ->
    println("process: handleGraphQLError()")
//    if (response == "token_expired") {
        throw Throwable(response.toString())
//    }
}

fun <T : Any> Observable<T>.handleError(): Observable<T> = onErrorResumeNext { response ->
    println("process: handleError()")
    Observable.never()
}


fun <T : Any> Observable<T>.refreshTokenIfNeedGraphQL(): Observable<T> = retryWhen { observable ->
    observable.flatMap { exception ->
        println("refreshTokenIfNeed: $exception")
        if (exception.message == "token_expired") {
            refreshTokenGraphQL()
        } else {
            Observable.error(exception)
        }
    }
}
