package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito

//UI Testing
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {


    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun initRepository() = runBlocking {
        stopKoin()
        appContext = getApplicationContext()

        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                SaveReminderViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }

        }

        startKoin {
            androidContext(appContext)
            modules(listOf(myModule))
        }

        //Get our real repository
        repository = get()


        repository.deleteAllReminders()


    }


    @After
    fun cleanupDb() = runBlockingTest {
        stopKoin()
    }

    @Test
    fun reminderListFragment_DisplayedInUi() {
        runBlocking {
            val reminder = ReminderDTO("title", "desc", "LA", 21.1, 21.1)
            repository.saveReminder(reminder)
            launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

            onView(withId(R.id.reminderssRecyclerView))
                .check(matches(hasDescendant(withText("title"))))
                .check(matches(hasDescendant(withText("desc"))))
                .check(matches(hasDescendant(withText("LA"))))
        }
    }

    @Test
    fun clickAddReminder_navigateSaveFragment() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())

        Mockito.verify(navController)
            .navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    @Test
    fun reminderList_showNoData() {
        runBlocking {

            launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)


            onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
            onView(withId(R.id.noDataTextView)).check(matches(withText(R.string.no_data)))
        }
    }

}