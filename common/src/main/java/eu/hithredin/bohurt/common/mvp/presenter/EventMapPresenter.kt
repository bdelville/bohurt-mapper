package eu.hithredin.bohurt.common.mvp.presenter

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import eu.hithredin.bohurt.common.data.EventData
import eu.hithredin.bohurt.common.data.EventQuery
import eu.hithredin.bohurt.common.mvp.view.EventMapView
import eu.hithredin.bohurt.common.mvp.viewmodel.SearchViewModel
import eu.hithredin.ktopendatasoft.ApiLoader
import eu.hithredin.ktopendatasoft.ListResult
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Present events in a map
 *
 * Communicate mainly by a SearchViewModel that contains search criterias
 */
class EventMapPresenter(val view: EventMapView, injector: KodeinInjector) : Presenter {
    private val logger = KotlinLogging.logger {}
    private val apiLoader: ApiLoader<EventData> by injector.instance()
    private val observeLoad = PublishSubject.create<Boolean>()
    private val disposables = CompositeDisposable()

    val searchVM: SearchViewModel

    init {
        val dateStart = Calendar.getInstance()
        dateStart.add(Calendar.MONTH, -1)
        val dateEnd = Calendar.getInstance()
        dateEnd.add(Calendar.MONTH, 6)
        val minDate = Calendar.getInstance()
        minDate.add(Calendar.MONTH, -36)
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.MONTH, 18)

        searchVM = SearchViewModel(dateStart.time, dateEnd.time, minDate, maxDate)
    }

    override fun screenClose() {
        disposables.clear()
    }

    override fun screenOpen() {
        view.displayDates(searchVM)

        disposables.add(
            observeLoad.throttleLast(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { view.showLoader(true) }
                .observeOn(Schedulers.io())
                .map {
                    EventQuery()
                        .dateStart(searchVM.dateStart)
                        .dateEnd(searchVM.dateEnd)
                        .rowCount(40)
                }
                .flatMapSingle { apiLoader.queryList(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::searchResult, { e -> logger.error { "observeLoad:\n$e" } }
                ))
    }

    private fun searchResult(result: Pair<Response, Result<ListResult<EventData>, FuelError>>) {
        logger.info { "Result query:\n$result" }
        view.showLoader(false)
        view.setEvents(emptyList(), false)

        result.second.success {
            view.setEvents(it.data()?.filter { event -> event.isValid() }.orEmpty(),
                true)
        }
        result.second.failure {
            view.showError("Loading error")
            logger.error { "Result query:\n$it" }
        }
    }

    fun setSearchDateRange(dateStart: Date, dateEnd: Date) {
        searchVM.dateStart = dateStart
        searchVM.dateEnd = dateEnd
        view.displayDates(searchVM)
        launchSearch()
    }

    fun setSearchText(text: String) {

    }

    fun launchSearch() {
        observeLoad.onNext(true)
    }
}