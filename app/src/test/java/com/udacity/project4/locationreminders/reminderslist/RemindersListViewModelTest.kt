package com.udacity.project4.locationreminders.reminderslist


import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {


//TODO: provide testing to the RemindersListViewModel and its live data objects

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var repo: FakeDataSource

    @get:Rule
    var instantExcutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        val reminderList = mutableListOf(
            ReminderDTO(
                "title", "description", "location", 10.0,
                10.0
            ), ReminderDTO(
                "title", "description", "location", 10.0,
                10.0
            )
        )
         repo = FakeDataSource(reminderList)
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), repo)

    }
    @After
    fun cleanupDataSource() = runBlocking {
        repo.deleteAllReminders()
        stopKoin()
    }

    @Test
    fun loadReminder_loadReminders_TwoReminder() = runBlockingTest {
        viewModel.loadReminders()

        val reminderList = viewModel.remindersList.getOrAwaitValue()

        assertThat(reminderList, hasSize(2))
    }

    @Test
    fun loadReminder_deleteAllReminder_0Reminder() = runBlockingTest {

        viewModel.deleteAllReminder()
        val reminderList = viewModel.remindersList.getOrAwaitValue()


        assertThat(reminderList, hasSize(0))
    }


}