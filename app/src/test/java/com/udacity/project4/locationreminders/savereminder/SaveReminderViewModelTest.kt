package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])

class SaveReminderViewModelTest {
    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var repo: FakeDataSource

    @get:Rule
    var instantExcutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

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
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), repo)

    }

    @After
    fun cleanupDataSource() = runBlocking {
        repo.deleteAllReminders()

        stopKoin()
    }

    @Test
    fun onClear_Null() = runBlocking {
        viewModel.onClear()
        assertThat(viewModel.reminderTitle.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.reminderDescription.getOrAwaitValue(), `is`(nullValue()))
        assertThat(viewModel.reminderSelectedLocationStr.getOrAwaitValue(), `is`(nullValue()))

    }

    @Test
    fun saveReminder_containReminderId() = runBlockingTest {

        val reminder = ReminderDataItem(
            "title", "description", "location",
            14.1, 14.2
        )
        viewModel.saveReminder(reminder)

        val result = (repo.getReminder(reminder.id) as Result.Success).data


        assertThat(result.id, `is`(reminder.id))

    }

    @Test
    fun testValidateEnteredData_TitleNull_returnsFalse() {
        val location = "loc"
        val reminderDataItem = ReminderDataItem(
            null, null, location,
            null, null
        )
        val isValid = viewModel.validateEnteredData(reminderDataItem)

        assertThat(isValid.toString(), `is`("false"))
        assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title)
        )
    }

    @Test
    fun testValidateEnteredData_locationNull_returnsFalse() {
        val title = "title"
        val reminderDataItem = ReminderDataItem(
            title, null, null,
            null, null
        )
        val isValid = viewModel.validateEnteredData(reminderDataItem)

        assertThat(isValid.toString(), `is`("false"))
        assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location)
        )
    }

    @Test
    fun testValidateEnteredData__returnsTrue() {

        val reminderDataItem = ReminderDataItem(
            "title", null, "location",
            null, null
        )
        val isValid = viewModel.validateEnteredData(reminderDataItem)

        assertThat(isValid.toString(), `is`("true"))

    }


}


