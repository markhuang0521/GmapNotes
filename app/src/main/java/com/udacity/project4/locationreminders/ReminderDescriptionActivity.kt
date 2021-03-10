package com.udacity.project4.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.navArgs
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.locationreminders.savereminder.selectreminderlocation.SelectLocationFragment
import com.udacity.project4.utils.addGeofence
import org.koin.android.ext.android.inject

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReminderDescriptionBinding
    private val args: ReminderDescriptionActivityArgs by navArgs()
    val _viewModel: SaveReminderViewModel by inject()
    private lateinit var reminder: ReminderDataItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

// notification reminder
        if (intent.hasExtra(EXTRA_ReminderDataItem)) {
            reminder = intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem
            binding.reminderDataItem = reminder
        } else {
            // onclick from recyclerview
            reminder = args.selectedReminder
            binding.reminderDataItem = reminder
        }

        binding.btnEditReminder.setOnClickListener {

//            reminder.title = _viewModel.reminderTitle.value
//            reminder.description = _viewModel.reminderDescription.value
//            reminder.location = _viewModel.reminderSelectedLocationStr.value
//            reminder.latitude = _viewModel.latitude.value
//            reminder.longitude = _viewModel.longitude.value
            Toast.makeText(this, reminder.toString(), Toast.LENGTH_SHORT).show()
            if (_viewModel.validateEnteredData(reminder)) {
                addGeofence(reminder, this)

                _viewModel.validateAndUpdateReminder(reminder)
            }
        }

    }

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }
}