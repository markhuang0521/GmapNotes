package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
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
    fun onClear_isNull()= runBlocking {
        viewModel.onClear()

    }



}