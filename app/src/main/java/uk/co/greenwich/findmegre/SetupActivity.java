package uk.co.greenwich.findmegre;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class SetupActivity extends AppCompatActivity {

    private Spinner DaySpinner, TimeSpinner;
    private EditText Location;
    private TextView TimeTableSummary;
    private TimeTable timetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.root_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        setupComponents();
    }

    private void setupComponents() {
        timetable = new TimeTable(this);
        DaySpinner = (Spinner) findViewById(R.id.Day);
        List<String> list = new ArrayList<String>();
        list.add("Sunday");
        list.add("Monday");
        list.add("Tuesday");
        list.add("Wednesday");
        list.add("Thursday");
        list.add("Friday");
        list.add("Saturday");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DaySpinner.setAdapter(dataAdapter);
        TimeSpinner = (Spinner) findViewById(R.id.Time);
        List<String> list2 = new ArrayList<String>();
        list2.add("0:00");
        list2.add("1:00");
        list2.add("2:00");
        list2.add("3:00");
        list2.add("4:00");
        list2.add("5:00");
        list2.add("6:00");
        list2.add("7:00");
        list2.add("8:00");
        list2.add("9:00");
        list2.add("10:00");
        list2.add("11:00");
        list2.add("12:00");
        list2.add("13:00");
        list2.add("14:00");
        list2.add("15:00");
        list2.add("16:00");
        list2.add("17:00");
        list2.add("18:00");
        list2.add("19:00");
        list2.add("20:00");
        list2.add("21:00");
        list2.add("22:00");
        list2.add("23:00");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TimeSpinner.setAdapter(dataAdapter2);
        TimeTableSummary = findViewById(R.id.TimeTableSummary);
        Location = findViewById(R.id.Location);
    }

    public void FinishedButton_OnClick(View view) {
        timetable.SaveTimeTableToFile();
        finish();
    }

    public void AddToTimetableButton_OnClick(View view) {
        int day = DaySpinner.getSelectedItemPosition();
        int hour24 = TimeSpinner.getSelectedItemPosition();
        String place = Location.getText().toString();
        timetable.AddEntryToTimeTable(day, hour24, place);
        TimeTableSummary.setText(TimeTableSummary.getText() + "\n Room : " + place + " Day : " + String.valueOf(DaySpinner.getSelectedItem()) + " Hour : " + hour24 + ":00");
        view.invalidate();
    }
}