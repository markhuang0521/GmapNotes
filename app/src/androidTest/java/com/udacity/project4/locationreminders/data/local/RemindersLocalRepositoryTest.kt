package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
//import com.udacity.project4.locationreminders.MainCoroutineRule

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    private lateinit var remindersRepository: RemindersLocalRepository
    private lateinit var remindersDao: RemindersDao
    private val testDispatcher = TestCoroutineDispatcher()

//    @get: Rule
//    var mainCoroutineRule = MainCoroutineRule()
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        remindersDao = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build().reminderDao()

        remindersRepository = RemindersLocalRepository(remindersDao, Dispatchers.Main)
        Dispatchers.setMain(testDispatcher)

    }

    @After
    fun closeDatabase() {
        testDispatcher.cleanupTestCoroutines()

    }

    @Test
    fun saveAndGetReminders_returnList() = runBlockingTest {
        val reminder = ReminderDTO("title", "desc", "LA", 21.1, 21.1)
        val reminder2 = ReminderDTO("title", "desc", "LA", 21.1, 21.1)

        remindersRepository.saveReminder(reminder)
        remindersRepository.saveReminder(reminder2)
        val result = remindersRepository.getReminders() as Result.Success<List<ReminderDTO>>

        assertThat(result.data.size, equalTo(2))
        assertThat(result.data, hasItem(reminder))
        assertThat(result.data, hasItem(reminder2))
        assertThat(result.data, notNullValue())

    }

    @Test
    fun getReminder_returnItem() = runBlockingTest {
        val reminder = ReminderDTO("title", "desc", "LA", 21.1, 21.1)
        remindersRepository.saveReminder(reminder)

        val result = remindersRepository.getReminder(reminder.id) as Result.Success<ReminderDTO>

        assertThat(result.data.id, equalTo(reminder.id))
        assertThat(result.data.title, equalTo(reminder.title))
        assertThat(result.data.latitude, equalTo(reminder.latitude))
        assertThat(result.data.longitude, equalTo(reminder.longitude))


    }

    @Test
    fun deleteReminders_return0() = runBlocking {
        val reminder = ReminderDTO("title", "desc", "LA", 21.1, 21.1)
        remindersRepository.saveReminder(reminder)

        remindersRepository.deleteAllReminders()

        val result = remindersRepository.getReminders() as Result.Success<List<ReminderDTO>>
        assertThat(result.data.size, equalTo(0))
    }

}
