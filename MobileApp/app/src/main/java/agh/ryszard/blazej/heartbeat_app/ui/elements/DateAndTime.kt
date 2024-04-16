package agh.ryszard.blazej.heartbeat_app.ui.elements

import agh.ryszard.blazej.heartbeat_app.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@Composable
@ExperimentalMaterial3Api
fun DateAndTimePreview(){
    val time = rememberTimePickerState()
    val date = rememberDatePickerState()
    DateAndTime(
        time = time,
        onClickTime = {},
        isTimeSelected = false,
        date = date,
        onClickDate = {},
        isDateSelected = false,
        label = "End date"
    )
}

@ExperimentalMaterial3Api
@Composable
fun DateAndTime (time: TimePickerState, onClickTime: () -> Unit, isTimeSelected: Boolean,
                 date: DatePickerState, onClickDate: () -> Unit, isDateSelected: Boolean,
                 label: String){

    Column(modifier = Modifier.padding(bottom = 12.dp)){
        Row {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DatePickerTextField(
                onClick = onClickDate,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.55f),
                isDateSelected = isDateSelected,
                date = date,
                leadingIcon = @Composable {
                    Icon(
                        painterResource(R.drawable.baseline_date_range_24),
                        contentDescription = "Calendar"
                    )
                },
                label = ""
            )
            TimepickerTextField(
                onClick = onClickTime,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.9f),
                isTimeSelected = isTimeSelected,
                leadingIcon = @Composable {
                    Icon(
                        painterResource(R.drawable.schedule_24px),
                        contentDescription = "Clock"
                    )
                },
                time = time,
                label = ""
            )
        }
    }
}