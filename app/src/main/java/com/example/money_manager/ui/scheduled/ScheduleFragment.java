package com.example.money_manager.ui.scheduled;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.money_manager.MainActivity;
import com.example.money_manager.R;

import com.example.money_manager.database.Schedule;
import com.example.money_manager.databinding.FragmentScheduleBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleFragment extends Fragment {
    private @NonNull FragmentScheduleBinding binding;
    private Calendar selectedDate;
    FirebaseAuth mAuth;
    View root;
    String formattedDate;
    String user;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_schedule, container, false);
        CalendarView calendarView = root.findViewById(R.id.calendarView);

        calendarView.setMinDate(System.currentTimeMillis());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = Calendar.getInstance();
                selectedDate.clear();
                selectedDate.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                formattedDate = sdf.format(selectedDate.getTime());
                List<Schedule> schedules = MainActivity.myDatabase.myDao().getSchedulesForDateAndUser(formattedDate, user);
                displaySchedules(schedules);
            }
        });
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        assert currentUser != null;
        user=currentUser.getUid();

        EditText valueEditText=root.findViewById(R.id.scheduleInput);
        Button saveButton = root.findViewById(R.id.scheduleSave);
        saveButton.setOnClickListener(v -> {
            String valueString = valueEditText.getText().toString().trim();
            if (!valueString.isEmpty()) {
                int value = Integer.parseInt(valueString);
                int sid=MainActivity.myDatabase.myDao().getLastSchedule();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                formattedDate = sdf.format(selectedDate.getTime());

                Schedule schedule=new Schedule();
                schedule.setSid(sid+1);
                schedule.setValue(value);
                schedule.setCat_name(user);
                schedule.setDate(String.valueOf(formattedDate));

                MainActivity.myDatabase.myDao().addSchedule(schedule);
                Toast.makeText(getActivity(), "Επιτυχής Προσθήκη", Toast.LENGTH_SHORT).show();
                valueEditText.setText("");

            } else {
                Toast.makeText(getActivity(), "Παρακαλώ πληκτρολογίστε ένα ποσό", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    private void displaySchedules(List<Schedule> schedules) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Schedule schedule : schedules) {
            stringBuilder.append("Ημερομηνία: ").append(schedule.getDate()).append(", Ποσό: ").append(schedule.getValue()).append("   \n");
        }
        TextView dispText=root.findViewById(R.id.displaySchedule);
        dispText.setText(stringBuilder.toString());
    }


}
