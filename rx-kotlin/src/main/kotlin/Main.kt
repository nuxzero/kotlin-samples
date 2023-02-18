import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toObservable

fun main() {
//    val messageArr = listOf("ant", "cat", "dog")
    var messageArr = emptyList<String>()

    Observable
        .fromIterable(messageArr)
        .flatMap { addPrefix(it).toObservable() }
        .doOnNext { println(it) }
        .zipWith(beforeCompleted(), { t1, t2 ->
            Pair(t1, t2)
        })
//        .subscribeBy(onNext = {
//            println("complete: $it")
//        })

    startProcess()
        .toObservable()
        .doOnNext { println(it) }
        .flatMap { Observable.fromIterable(messageArr) }
        .switchMapMaybe {
            Maybe.just(it)
        }
        .flatMap {
            addPrefix(it).toObservable()
        }
        .doOnNext { println(it) }
        .zipWith(completeMessage().toObservable()) { _, _ -> }
        .flatMap { beforeCompleted() }
        .doOnNext { println(it) }
//        .subscribeBy(
//            onComplete = { println("subscribeBy.onComplete") },
//            onNext = { println("subscribeBy.onNext: $it") })

    // Empty list
    val completableA = Completable.complete()
    val completableB = Completable.complete()

    completableA
        .doOnComplete { println("completeA") }
        .andThen(completableB)
        .doOnComplete { println("completeB") }
        .subscribeBy { println("completed") }

    Completable
        .fromSingle(startProcess())
        .doOnComplete { println("startProcess completed") }
        .andThen (
            Completable.fromObservable(Observable
                .fromIterable(messageArr)
                .flatMap {
                    addPrefix(it).toObservable()
                })
        )
        .doOnComplete { println("messageArr completed") }
        .andThen(Completable.fromSingle(endProcess()))
        .doOnComplete { println("endProcess completed") }
        .subscribeBy(
            onComplete = { println("subscribeBy.onComplete") },
            onError = { println("subscribeBy.onError $it") },
        )
}

fun addPrefix(message: String): Single<String> = Single.just("pre-$message")

//fun beforeComplete(): Single<String> = Single.just("Process completed!")

fun beforeCompleted(): Observable<Boolean> = Observable.just(true)

fun endProcess(): Single<String> = Single.just("End process...")

fun completeMessage(): Single<String> = Single.just("Finished")

fun startProcess(): Single<String> = Single.just("Start process...")
