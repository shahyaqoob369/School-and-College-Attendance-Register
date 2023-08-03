package com.acode.attendanceHome.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acode.attendanceHome.AdManager;
import com.acode.attendanceHome.BaseActivity;
import com.acode.attendanceHome.MainActivity;
import com.acode.attendanceHome.R;
import com.acode.attendanceHome.StudentReportActivity;
import com.acode.attendanceHome.dialogs.AddSingleStudent;
import com.acode.attendanceHome.dialogs.AddStudentGroup;
import com.acode.attendanceHome.dialogs.EditStudent;
import com.acode.attendanceHome.recyclerview.StudentListAdopter;
import com.acode.attendanceHome.roomDataBase.Attendance;
import com.acode.attendanceHome.roomDataBase.DailyAttendance;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StudentListFragment extends Fragment implements
        StudentListAdopter.StudentRecyclerOnClickListener,
        StudentListAdopter.PassCurrentAttendance
        , View.OnClickListener {
    private static final String TAG = "StudentListFragment";
    private static final int PERMISSION_REQUEST_CODE = 101;
    public StudentListAdopter studentListAdopter;
    private String classname;
    FloatingActionsMenu add_menu_fbtn;
    FloatingActionButton add_single_student_fbtn, add_group_student_fbtn, add_list_student_fbtn;
    private List<Attendance> studentList = new ArrayList();
    public static List<DailyAttendance> listForEdit = new ArrayList();
    RecyclerView recyclerview;
    TextView strengthCounter_tv;
    SearchView searchView;
    RelativeLayout ttlayout;
    LinearLayout help_layOut;
    CardView bottomMenu_Card;
    public static Attendance signalSelectedItem = null;
    ArrayList<Attendance> filteredList;
    TextView struckOff;
    TextView edit;


    //AdMob ads
    private AdView bannerAd_List;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student, container, false);

        //onClickListener on add Button:
        add_single_student_fbtn = view.findViewById(R.id.add_single_student_fbtn);
        add_group_student_fbtn = view.findViewById(R.id.add_group_student_fbtn);
        add_list_student_fbtn = view.findViewById(R.id.add_list_student_fbtn);
        strengthCounter_tv = view.findViewById(R.id.strengthCounter_tv);
        searchView = view.findViewById(R.id.searchView);
        add_menu_fbtn = view.findViewById(R.id.add_menu_fbtn);
        ttlayout = view.findViewById(R.id.tt_strentlayout);
        bottomMenu_Card = view.findViewById(R.id.bottomMenu_Card);
        struckOff = view.findViewById(R.id.menu_struck_off);
        edit = view.findViewById(R.id.menu_longPress_edit);
        help_layOut = view.findViewById(R.id.help_layOut);


        //adMob BannerAd
        bannerAd_List = view.findViewById(R.id.bannerAd_List);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAd_List.loadAd(adRequest);


        struckOff.setOnClickListener(this);
        edit.setOnClickListener(this);

        add_single_student_fbtn.setOnClickListener(v ->

        {
            addSingleStudent();
            add_menu_fbtn.collapse();
        });

        add_group_student_fbtn.setOnClickListener(v ->

        {
            addStudentGroup();
            add_menu_fbtn.collapse();
        });

        add_list_student_fbtn.setOnClickListener(v ->

        {
            add_menu_fbtn.collapse();
            importExcelDialog();
        });

        //for get class name and subject through intent
        Intent intent = requireActivity().getIntent();
        classname = intent.getStringExtra(MainActivity.CLASS_NAME);
        Log.d(TAG, "onCreateView: " + classname);


        recyclerview = view.findViewById(R.id.studentFragment_recyclerview_xml);
        BaseActivity.mViewModel.getallStudentsByClsNm(classname).

                observe(requireActivity(), attendances -> {
                    ConstraintLayout rootV = view.findViewById(R.id.rootV);
                    if (!attendances.isEmpty()) {
                        strengthCounter_tv.setText(String.valueOf(attendances.size()));
                        studentList = attendances;
                        studentListAdopter = new StudentListAdopter(
                                getContext(),
                                rootV,
                                attendances,
                                BaseActivity.mViewModel,
                                bottomMenu_Card,
                                this,
                                this);
                        recyclerview.setLayoutManager(new LinearLayoutManager(requireActivity()));
                        recyclerview.setAdapter(studentListAdopter);
                        if (studentList != null) {
                            if (!studentList.isEmpty()) {
                                help_layOut.setVisibility(View.GONE);
                                recyclerview.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        //SearchView Methods:
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            ttlayout.setVisibility(View.VISIBLE);
            if (filteredList != null) {
                filteredList.clear();
            }
            filteredList = null;
            studentListAdopter.filterList((ArrayList<Attendance>) studentList);
            return false;
        });

        searchView.setOnSearchClickListener(v -> ttlayout.setVisibility(View.GONE));


        return view;
    }


    private void importButtonClick() {
        importList();
    }

    public void addSingleStudent() {
        DialogFragment dialogFragment = new AddSingleStudent(studentList, getActivity());
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "addStudent");
    }

    public void addStudentGroup() {
        DialogFragment dialogFragment = new AddStudentGroup();
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "addStudentGroup");
    }

    @Override
    public void StudentListOnClick(int position, View view) {
        Log.d(TAG, "StudentListOnClick: clicked on recycler");

        String name;
        int rn;
        String cls;
        if (filteredList != null && !filteredList.isEmpty()) {
            name = filteredList.get(position).getName();
            rn = filteredList.get(position).getRollNo();
            cls = filteredList.get(position).getClassName();
        } else {
            name = studentList.get(position).getName();
            rn = studentList.get(position).getRollNo();
            cls = studentList.get(position).getClassName();
        }
        Intent intent = new Intent(requireActivity(), StudentReportActivity.class);
        intent.putExtra(BaseActivity.EXTRA_NAME, name);
        intent.putExtra(BaseActivity.EXTRA_RN, rn);
        intent.putExtra(BaseActivity.EXTRA_CLS, cls);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.click_anim);
        view.startAnimation(animation);
        startActivity(intent);


        //AdMob InterstitialAd:
        if (AdManager.mInterstitialAd != null) {
            AdManager.mInterstitialAd.show(requireActivity());

            AdManager.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    AdManager.mInterstitialAd = null;
                    AdManager.loadInterstitial(requireActivity());
                }
            });
        }

    }

    public void EditStudent(Attendance attendance) {
        DialogFragment dialogFragment = new EditStudent(attendance);
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "addStudent");
    }

    private void filter(String text) {
        filteredList = new ArrayList<>();
        for (Attendance item : studentList) {
            String rn = String.valueOf(item.getRollNo());
            if (item.getName().toLowerCase().contains(text.toLowerCase()) || rn.contains(text)) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(requireActivity(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            studentListAdopter.filterList(filteredList);
        }
    }

    private void checkStruckOff(Attendance cStudent) {
        if (cStudent.isStruckOff()) {
            Attendance newAtt = new Attendance(
                    cStudent.getS_no(),
                    cStudent.getName(),
                    cStudent.getRollNo(),
                    cStudent.getClassName(),
                    cStudent.getStudent_status(),
                    false
            );
            BaseActivity.mViewModel.insert(newAtt);
            Toast.makeText(getActivity(), "Re-Admit", Toast.LENGTH_SHORT).show();
        } else {
            Attendance newAtt = new Attendance(
                    cStudent.getS_no(),
                    cStudent.getName(),
                    cStudent.getRollNo(),
                    cStudent.getClassName(),
                    cStudent.getStudent_status(),
                    true
            );
            BaseActivity.mViewModel.insert(newAtt);
            Toast.makeText(getActivity(), "StruckOff", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPassClick(Attendance attendance) {
        bottomMenu_Card.setVisibility(View.VISIBLE);
        if (attendance.isStruckOff()) struckOff.setText(R.string.re_admit);
        else struckOff.setText(R.string.struck_off);
        signalSelectedItem = attendance;
        Log.d(TAG, "onPassClick: clickd");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_struck_off:
                checkStruckOff(signalSelectedItem);
                break;
            case R.id.menu_longPress_edit:
                //Edit in the student code is here:
                getEditStudentAttendanceList(signalSelectedItem.getName(), signalSelectedItem.getRollNo());
                EditStudent(signalSelectedItem);
                break;
        }
    }

    public static List<DailyAttendance> getEditStudentList() {
        Log.d(TAG, "getEditStudentList: size " + listForEdit.size());
        return listForEdit;
    }

    private void getEditStudentAttendanceList(String classname, int rolno) {

        BaseActivity.mViewModel.getAllDailyAttendanceStudentName(classname, rolno).observe(requireActivity(), list -> {
            listForEdit = list;
            Log.d(TAG, "getEditStudentAttendanceList: size " + listForEdit.size());
        });
    }

    //FilePickers to import list of Students:
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    assert intent != null;
                    Uri uri = intent.getData();
                    String p = getDriveFile(requireActivity(), uri);
                    readExcel(p);
                }
            }
    );


    public void importList() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/vnd.ms-excel");
        intent = Intent.createChooser(intent, "Choose a file");
        activityResultLauncher.launch(intent);


    }

    //ReadExcel Sheet to import EXCEL LIST:
    public void readExcel(String path) {

        try {

            InputStream myInput;

            myInput = new FileInputStream(path);
            // Create a POI File System object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            // We now need something to iterate through the cells.
            Iterator<Row> rowIter = mySheet.rowIterator();
            int rowno = 0;
            while (rowIter.hasNext()) {
                Log.e(TAG, " row no " + rowno);
                HSSFRow myRow = (HSSFRow) rowIter.next();
                if (rowno != 0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno = 0;
                    String sno = "", name = "", rollno = "";
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno == 0) {
                            sno = myCell.toString();
                        } else if (colno == 1) {
                            name = myCell.toString();
                        } else if (colno == 2) {
                            rollno = myCell.toString();
                        }
                        colno++;
                        Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell);
                    }

                    String[] s = rollno.split("\\.");
                    int rn = Integer.parseInt(s[0]);


                    Attendance newStudent
                            = new Attendance(name, rn, classname, "Present", false);
                    Log.d(TAG, "newStudent " + name + rollno);
                    BaseActivity.mViewModel.insert(newStudent);

                }
                rowno++;
            }

        } catch (Exception e) {
            Log.d(TAG, "ReadExcelDemo: got Error = " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Sample Sheet to import EXCEL LIST dialog:
    private void importExcelDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Sample Sheet Example");
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_excel_list_check, null);
        alertDialog.setView(customLayout);
        alertDialog.setPositiveButton("IMPORT", (dialog, which) -> importButtonClick());
        alertDialog.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public static String getDriveFile(Context context, Uri uri) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.d("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.d("File Path", "Path " + file.getPath());
            Log.d("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();

    }


}