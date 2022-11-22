package com.kma.taskmanagement.ui.main.fragments;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.broadcastReceiver.AlarmBroadcastReceiver;
import com.kma.taskmanagement.data.local.DatabaseHelper;
import com.kma.taskmanagement.data.model.Category;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.repository.CategoryRepository;
import com.kma.taskmanagement.data.repository.TaskRepository;
import com.kma.taskmanagement.data.repository.impl.CategoryRepositoryImpl;
import com.kma.taskmanagement.data.repository.impl.TaskRepositoryImpl;
import com.kma.taskmanagement.listener.HandleClickListener;
import com.kma.taskmanagement.ui.adapter.CustomAdapter;
import com.kma.taskmanagement.ui.adapter.TaskAdapter;
import com.kma.taskmanagement.ui.common.CustomSpinner;
import com.kma.taskmanagement.ui.main.CategoryViewModel;
import com.kma.taskmanagement.ui.main.CategoryViewModelFactory;
import com.kma.taskmanagement.ui.main.TaskViewModel;
import com.kma.taskmanagement.ui.main.TaskViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.List;

public class PersonTaskFragment extends Fragment {

    RecyclerView taskRecycler;
    TextView addTask;
    ImageView calendar;
    ImageView ivAction, ivFind, ivFilter, ivRefresh;
    LinearLayout linearLayout;
    TaskAdapter taskAdapter;
    CustomSpinner dropdown, spinnerPrio, spinnerStatus;
    LinearLayout llAnimation;
    List<Task> taskList = new ArrayList<>();
    CustomAdapter customAdapter;
    Dialog filterDialog;
    TextView okay_text, cancel_text;
    ShowCalendarViewBottomSheet showCalendarViewBottomSheet;
    String token = "";
    long cateId;
    private CategoryViewModel categoryViewModel;
    private TaskViewModel taskViewModel;
    private CategoryRepository categoryRepository = new CategoryRepositoryImpl();
    private TaskRepository taskRepository = new TaskRepositoryImpl();
    private ProgressDialog progressDialog;
    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;
    private DatabaseHelper db;
    List<Category> categories = new ArrayList<>();
    String[] prios = new String[]{"", "LOWEST", "MEDIUM", "HIGH"};
    String[] statuss = new String[]{"", "TODO", "DOING", "COMPLETED"};

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
        db = new DatabaseHelper(requireActivity());
        //taskViewModel.getAllTasks(Constants.BEARER + token);
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
                if(s.equals("Hoàn thành")) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                }
                progressDialog.setMessage(s);
                progressDialog.show();
            }
        });
        categoryViewModel.getResult().observe(getActivity(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories.size() == 0) {
                } else {
                    //int index = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getIntFromSharedPreferences(Constants.CATE_INDEX + GlobalInfor.username);
                    customAdapter.setList(categories);
                    //dropdown.setSelection(index);
                }
            }
        });
        taskViewModel.getResponse().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals("Hoàn thành")) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                }
                progressDialog.setMessage(s);
                progressDialog.show();
            }
        });
        taskViewModel.getResult().observe(getActivity(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                if(tasks.size() != 0) {
                    llAnimation.setVisibility(View.GONE);
                    taskRecycler.setVisibility(View.VISIBLE);
                    taskList = tasks;
                    taskAdapter.setList(taskList);
                    showCalendarViewBottomSheet.setList(taskList);
                } else {
                    llAnimation.setVisibility(View.VISIBLE);
                    taskRecycler.setVisibility(View.GONE);
                }
            }
        });
        taskViewModel.getSyncResult().observe(getActivity(), new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                if(task != null) {
                    db.updateTaskStatus(task.getName(), 1);
                }
            }
        });

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ComponentName receiver = new ComponentName(getActivity(), AlarmBroadcastReceiver.class);
        PackageManager pm = getActivity().getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        addTask.setOnClickListener(view1 -> {
            if(customAdapter.getCount() == 0) {
                Toast.makeText(TaskApplication.getAppContext(), "Chưa tạo danh mục", Toast.LENGTH_SHORT).show();
                return;
            }
            CreateTaskBottomSheetFragment createTaskBottomSheetFragment = new CreateTaskBottomSheetFragment();
            createTaskBottomSheetFragment.setTaskAction(false, customAdapter.getItem(dropdown.getSelectedItemPosition()).getId(), null);
            createTaskBottomSheetFragment.show(getActivity().getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
        });

        enableSwipeToDelete();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Cursor cursor = db.getUnsyncedTasks();
                if (cursor.moveToFirst()) {
                    do {
                        long cateId =  cursor.getInt(10);
                        String desc = cursor.getString(3);
                        String endDateFormat = cursor.getString(4);
                        String title = cursor.getString(5);
                        String prio = cursor.getString(7);
                        String startDateFormat = cursor.getString(8);
                        String status = cursor.getString(9);
                        taskViewModel.addTask(Constants.BEARER + token, new Task("", cateId, "", desc, endDateFormat, null,  title, GlobalInfor.username, prio,  startDateFormat, status, null));
                    } while (cursor.moveToNext());
                }
                GlobalInfor.IS_SEND = true;
            }
        };

        //registering the broadcast receiver to update sync status
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constants.DATA_SAVED_BROADCAST));
    }

    public void initView(View view) {
        filterDialog = new Dialog(getActivity());
        taskRecycler = view.findViewById(R.id.taskRecycler);
        addTask = view.findViewById(R.id.addTask);
        linearLayout = view.findViewById(R.id.linearLayout);
        ivAction = view.findViewById(R.id.ivAction);
        ivFind = view.findViewById(R.id.ivFind);
        ivFilter = view.findViewById(R.id.ivFilter);
        ivRefresh = view.findViewById(R.id.ivRefresh);
        calendar = view.findViewById(R.id.calendar);
        dropdown = view.findViewById(R.id.spinner1);
        llAnimation = view.findViewById(R.id.llAnimation);
        showCalendarViewBottomSheet = new ShowCalendarViewBottomSheet();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
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

        ivRefresh.setOnClickListener(view -> {
            taskViewModel.getAllTasks(Constants.BEARER + token);
        });
        ivFilter.setOnClickListener(view -> {
            openFilterDialog();
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                cateId = customAdapter.getItem(dropdown.getSelectedItemPosition()).getId();
                taskAdapter.setCateId(customAdapter.getItem(dropdown.getSelectedItemPosition()).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });
        dropdown.setLongClickable(true);
        dropdown.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                showConfirm();
                return true;
            }}
        );
        calendar.setOnClickListener(view -> {
//            showCalendarViewBottomSheet = new ShowCalendarViewBottomSheet();
//            showCalendarViewBottomSheet.setList(taskList);
            showCalendarViewBottomSheet.show(getChildFragmentManager(), showCalendarViewBottomSheet.getTag());
        });
    }

    public void showConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Xóa danh mục?")
                .setTitle("Xác nhận");

        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               categoryViewModel.deleteCategory(Constants.BEARER + token, cateId);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        categoryViewModel.getAllCategories(Constants.BEARER + token);
                    }
                },1000);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
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
                    String cate = editText.getText().toString().trim();
                    if(cate.length() == 0) {
                        Toast.makeText(getActivity(), "Chưa nhập danh mục", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Category category = new Category();
                    category.setName(cate);
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
        taskAdapter = new TaskAdapter(getContext(), taskList, new HandleClickListener() {
            @Override
            public void onLongClick(View view) {

            }

            @Override
            public void onTaskClick(Task task, String status) {
                task.setStatus(status);
                task.setCategory_id(cateId);
                taskViewModel.update(Constants.BEARER + token, task.getId(), task);
            }
        });
        taskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        taskRecycler.setAdapter(taskAdapter);
    }

    private void enableSwipeToDelete() {
            SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity().getApplicationContext()) {
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    final int position = viewHolder.getAdapterPosition();
                    final Task task = taskAdapter.taskList.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage("Xóa công việc?")
                            .setTitle("Xác nhận");

                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            taskViewModel.deleteTask(Constants.BEARER + token, task.getId());

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
                                    long id = customAdapter.getItem(dropdown.getSelectedItemPosition()).getId();
                                    taskViewModel.getTasksByCategory(Constants.BEARER + token, id);
                                }
                            },500);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    long id = customAdapter.getItem(dropdown.getSelectedItemPosition()).getId();
                                    taskViewModel.getTasksByCategory(Constants.BEARER + token, id);                                }
                            },500);
                        }
                    });


                    AlertDialog dialog = builder.create();
                    dialog.show();

//                    taskViewModel.deleteTask(Constants.BEARER + token, task.getId());
//
//                    Intent intent  = new Intent(getActivity(), AlarmBroadcastReceiver.class);
//                    intent.setAction(task.getName());
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
//                    if(pendingIntent != null) {
//                        alarmManager.cancel(pendingIntent);
//                    }
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            taskViewModel.getAllTasks(Constants.BEARER + token);
//                        }
//                    },500);
                }
            };

            ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
            itemTouchhelper.attachToRecyclerView(taskRecycler);

    }

    private void openFilterDialog() {
        filterDialog.setContentView(R.layout.layout_filter_dialog);
        filterDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        filterDialog.setCancelable(false);

        okay_text = filterDialog.findViewById(R.id.okay_text);
        cancel_text = filterDialog.findViewById(R.id.cancel_text);
        spinnerPrio = filterDialog.findViewById(R.id.filterPrio);
        spinnerStatus = filterDialog.findViewById(R.id.filterStatus);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, statuss);
        spinnerStatus.setAdapter(statusAdapter);

        ArrayAdapter<String> prioAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, prios);
        spinnerPrio.setAdapter(prioAdapter);

        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = spinnerStatus.getSelectedItem().toString();
                String prio = spinnerPrio.getSelectedItem().toString();
                if(!status.equals("") && prio.equals("")) {
                    taskViewModel.filterPersonalTaskByStatus(Constants.BEARER + token, cateId, status);
                } else if(status.equals("") && !prio.equals("")) {
                    taskViewModel.filterPersonalTaskByPrio(Constants.BEARER + token, cateId, prio);
                } else if(!status.equals("") && !prio.equals("")) {
                    taskViewModel.filterPersonalTaskByPrioAndStatus(Constants.BEARER + token, cateId, prio, status);
                }
                filterDialog.dismiss();
            }
        });

        cancel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });

        filterDialog.show();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        cateId = customAdapter.getItem(dropdown.getSelectedItemPosition()).getId();
//    }

    @Override
    public void onPause() {
        super.onPause();
        //dropdown.onDetachedFromWindow();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().unregisterReceiver(broadcastReceiver);
    }
}
