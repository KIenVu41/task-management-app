package com.kma.taskmanagement.ui.main.fragments;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.broadcastReceiver.AlarmBroadcastReceiver;
import com.kma.taskmanagement.data.model.Category;
import com.kma.taskmanagement.data.model.SubTask;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.repository.CategoryRepository;
import com.kma.taskmanagement.data.repository.TaskRepository;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.CategoryRepositoryImpl;
import com.kma.taskmanagement.data.repository.impl.TaskRepositoryImpl;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.listener.HandleClickListener;
import com.kma.taskmanagement.ui.adpater.CustomAdapter;
import com.kma.taskmanagement.ui.adpater.TaskAdapter;
import com.kma.taskmanagement.ui.common.CustomSpinner;
import com.kma.taskmanagement.ui.intro.IntroActivity;
import com.kma.taskmanagement.ui.main.CategoryViewModel;
import com.kma.taskmanagement.ui.main.CategoryViewModelFactory;
import com.kma.taskmanagement.ui.main.TaskViewModel;
import com.kma.taskmanagement.ui.main.TaskViewModelFactory;
import com.kma.taskmanagement.ui.user.LoginActivity;
import com.kma.taskmanagement.ui.user.UserViewModel;
import com.kma.taskmanagement.ui.user.UserViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.internal.concurrent.TaskLoggerKt;

public class PersonTaskFragment extends Fragment {

    RecyclerView taskRecycler;
    TextView addTask;
    ImageView noDataImage;
    ImageView ivAction, ivFind;
    LinearLayout linearLayout;
    TaskAdapter taskAdapter;
    CustomSpinner dropdown;
    LinearLayout llAnimation;
    List<Task> taskList = new ArrayList<>();
    public static int count = 0;
    AlarmManager alarmManager;
    CustomAdapter customAdapter;
    String token = "";
    private CategoryViewModel categoryViewModel;
    private TaskViewModel taskViewModel;
    private CategoryRepository categoryRepository = new CategoryRepositoryImpl();
    private TaskRepository taskRepository = new TaskRepositoryImpl();
    List<Category> categories = new ArrayList<>();

    public PersonTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        categoryViewModel =  new ViewModelProvider(requireActivity(), new CategoryViewModelFactory(categoryRepository)).get(CategoryViewModel.class);
        taskViewModel = new ViewModelProvider(requireActivity(), new TaskViewModelFactory(taskRepository)).get(TaskViewModel.class);
        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
        categoryViewModel.getAllCategories(Constants.BEARER + token);
        taskViewModel.getAllTasks(Constants.BEARER + token);
        return inflater.inflate(R.layout.fragment_person_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        setOnClick();
        setUpAdapter();
        categoryViewModel.getResponse().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    Log.d("TAG", s);
                }
            }
        });
        categoryViewModel.getResult().observe(getActivity(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories.size() == 0) {
                } else {
                    Log.d("TAG", categories.toString());
                    //int index = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getIntFromSharedPreferences(Constants.CATE_INDEX + GlobalInfor.username);
                    customAdapter.setList(categories);
                    //dropdown.setSelection(index);
                }
            }
        });
        taskViewModel.getResponse().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    Log.d("TAG", s);
                }
            }
        });
        taskViewModel.getResult().observe(getActivity(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                if(tasks.size() != 0 && tasks != null) {
                    llAnimation.setVisibility(View.GONE);
                    taskRecycler.setVisibility(View.VISIBLE);
                    taskList = tasks;
                    taskAdapter.setList(taskList);
                } else {
                    llAnimation.setVisibility(View.VISIBLE);
                    taskRecycler.setVisibility(View.GONE);
                }
            }
        });
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ComponentName receiver = new ComponentName(getActivity(), AlarmBroadcastReceiver.class);
        PackageManager pm = getActivity().getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        addTask.setOnClickListener(view1 -> {
            CreateTaskBottomSheetFragment createTaskBottomSheetFragment = new CreateTaskBottomSheetFragment();
            createTaskBottomSheetFragment.setTaskAction(false, customAdapter.getItem(dropdown.getSelectedItemPosition()).getId());
            createTaskBottomSheetFragment.show(getActivity().getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
        });

        enableSwipeToDelete();
    }

    public void initView(View view) {
        taskRecycler = view.findViewById(R.id.taskRecycler);
        addTask = view.findViewById(R.id.addTask);
        linearLayout = view.findViewById(R.id.linearLayout);
        ivAction = view.findViewById(R.id.ivAction);
        ivFind = view.findViewById(R.id.ivFind);
        dropdown = view.findViewById(R.id.spinner1);
        llAnimation = view.findViewById(R.id.llAnimation);

        dropdown.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
            @Override
            public void onPopupWindowOpened(Spinner spinner) {
                dropdown.setBackground(getResources().getDrawable(R.drawable.bg_spinner_up));
            }

            @Override
            public void onPopupWindowClosed(Spinner spinner) {
                dropdown.setBackground(getResources().getDrawable(R.drawable.bg_spinner));
            }
        });
        customAdapter=new CustomAdapter(getActivity(), categories);

        dropdown.setAdapter(customAdapter);
    }

    public void setOnClick() {
        ivAction.setOnClickListener(view -> {
            showAlertDialogButtonClicked(getResources().getString(R.string.create), 0, "", false);
        });

        ivAction.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showAlertDialogButtonClicked(getResources().getString(R.string.edit), customAdapter.getItem(dropdown.getSelectedItemPosition()).getId(), customAdapter.getItem(dropdown.getSelectedItemPosition()).getName(), true);
                return true;
            }
        });
        ivFind.setOnClickListener(view -> {
            long id = customAdapter.getItem(dropdown.getSelectedItemPosition()).getId();
            taskViewModel.getTasksByCategory(Constants.BEARER + token, id);
        });
    }

    public void showAlertDialogButtonClicked(String action, long id, String name, boolean isEdit)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.title));

        final View customLayout = getLayoutInflater().inflate(R.layout.custom_category_layout, null);
        builder.setView(customLayout);

        EditText editText = customLayout.findViewById(R.id.edtName);
        editText.setText(name);
        int position = dropdown.getSelectedItemPosition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).storeIntInSharedPreferences(Constants.CATE_INDEX + GlobalInfor.username, position);
        }

        builder.setPositiveButton(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
                {
                    Category category = new Category();
                    category.setName(editText.getText().toString());
                    if(!isEdit) {
                        categoryViewModel.addCategory(Constants.BEARER + token, category);
                    } else {
                        categoryViewModel.updateCategory(Constants.BEARER + token, id, category );
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            categoryViewModel.getAllCategories(Constants.BEARER + token);
                        }
                        },1000);
                }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setUpAdapter() {
        taskAdapter = new TaskAdapter(getContext(), taskList);
        taskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        taskRecycler.setAdapter(taskAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createAnAlarm() {
        try {
            String[] items1 = "03-10-2022".toString().split("-");
            String dd = items1[0];
            String month = items1[1];
            String year = items1[2];

            String[] itemTime = "20:15".toString().split(":");
            String hour = itemTime[0];
            String min = itemTime[1];
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour)); // hour
            cal.set(Calendar.MINUTE, Integer.parseInt(min)); // min
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DATE, Integer.parseInt(dd)); //dd

            if (cal.getTimeInMillis() < System.currentTimeMillis()) {
                return;
            }

            Intent alarmIntent = new Intent(getActivity(), AlarmBroadcastReceiver.class);
            alarmIntent.putExtra("TITLE", "xxx");
            alarmIntent.putExtra("DESC", "ss");
            alarmIntent.putExtra("DATE", "zzz");
            alarmIntent.putExtra("TIME", "zzz");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), count, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                }
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableSwipeToDelete() {
            SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity().getApplicationContext()) {
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    final int position = viewHolder.getAdapterPosition();
                    final Task task = taskAdapter.taskList.get(position);

                    taskViewModel.deleteTask(Constants.BEARER + token, task.getId());
//                    taskAdapter.taskList.remove(position);
//                   taskAdapter.notifyItemRemoved(position);
                    Intent intent  = new Intent(getActivity(), AlarmBroadcastReceiver.class);
                    intent.setAction(task.getName());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                    if(pendingIntent != null) {
                        alarmManager.cancel(pendingIntent);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            taskViewModel.getAllTasks(Constants.BEARER + token);
                        }
                    },500);
                }
            };

            ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
            itemTouchhelper.attachToRecyclerView(taskRecycler);

        }

    @Override
    public void onPause() {
        super.onPause();
        //dropdown.onDetachedFromWindow();
    }
}
