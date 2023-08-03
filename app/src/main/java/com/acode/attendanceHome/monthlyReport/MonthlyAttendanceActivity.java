package com.acode.attendanceHome.monthlyReport;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.acode.attendanceHome.AdManager;
import com.acode.attendanceHome.BaseActivity;
import com.acode.attendanceHome.BuildConfig;
import com.acode.attendanceHome.R;
import com.acode.attendanceHome.roomDataBase.Attendance;
import com.acode.attendanceHome.roomDataBase.DailyAttendance;
import com.google.android.gms.ads.FullScreenContentCallback;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

public class MonthlyAttendanceActivity extends AppCompatActivity {

    private static final String TAG = "MonthlyActivity";
    private List<DailyAttendance> attendanceList = new ArrayList<>();
    private List<Attendance> studentList = new ArrayList<>();

    LinearLayout report_progressBar;
    private TextView prog_tv;

    long[] idArray;
    String[] nameArray;
    int[] rollArray;
    int DAY_IN_MONTH;
    int rowSize;

    String month;
    String fullDate;

    String className;
    String path;
    String fileName = "Monthly_Attendance_Sheet_";
    ScrollView sheetView;
    File myPath;
    String imgURi;
    private Bitmap bitMapp;
    private int totalWeight = 0;
    private int totalHeight = 0;

    private int total_P = 0;
    private int total_A = 0;
    private int total_L = 0;
    private int p_percentageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_attendance);

        month = getIntent().getStringExtra("month");
        fullDate = getIntent().getStringExtra("date");
        className = getIntent().getStringExtra("cn");

        //Set ActionBar:
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'> " + className + " " + month + " </font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color_main)));


        report_progressBar = findViewById(R.id.report_progressBar);
        prog_tv = findViewById(R.id.prog_tv);

        fileName = className + " " + month + " Attendance_Sheet_";
        Button savePdf = findViewById(R.id.savePdf);
        savePdf.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveReportDialog();
            } else {
                if (!checkPermissions()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Allow AttendanceHome to save file in storage?");
                    builder.setPositiveButton("Allow", (dialog, which) -> requestPermission());
                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                    builder.show();

                } else {
                    saveReportDialog();
                }
            }
        });

        attendanceList = BaseActivity.mViewModel.getMonthlyWiseData(month, className);

        BaseActivity.mViewModel.getallStudentsByClsNm(className).observe(this, studentList -> {
            this.studentList = studentList;
            showTable();
        });
    }

    private void showTable() {
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        try {

            idArray = new long[studentList.size()];
            nameArray = new String[studentList.size()];
            rollArray = new int[studentList.size()];
            DAY_IN_MONTH = getNumberOfDayInMonth(getMonth(fullDate));
            rowSize = idArray.length + 1;

            for (int i = 0; i < studentList.size(); i++) {
                nameArray[i] = studentList.get(i).getName();
                rollArray[i] = studentList.get(i).getRollNo();
            }

            // row setup
            TableRow[] rows = new TableRow[rowSize];
            TextView[] roll_tvs = new TextView[rowSize];
            TextView[] name_tvs = new TextView[rowSize];
            TextView[][] status_tvs = new TextView[rowSize][DAY_IN_MONTH + 6];

            for (int i = 0; i < rowSize; i++) {
                roll_tvs[i] = new TextView(this);
                name_tvs[i] = new TextView(this);

                for (int j = 1; j <= DAY_IN_MONTH + 5; j++) {
                    status_tvs[i][j] = new TextView(this);
                }
            }

            // header
            roll_tvs[0].setText(R.string.rollNo);
            roll_tvs[0].setTypeface(roll_tvs[0].getTypeface(), Typeface.BOLD);
            name_tvs[0].setText(R.string.name);
            name_tvs[0].setTypeface(name_tvs[0].getTypeface(), Typeface.BOLD);

            for (int i = 1; i <= DAY_IN_MONTH + 6; i++) {
                if (i <= DAY_IN_MONTH) {
                    status_tvs[0][i].setText(String.valueOf(i));
                    status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(), Typeface.BOLD);
                } else if (i == DAY_IN_MONTH + 1) {
                    status_tvs[0][i].setText(R.string.total_P);
                    status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(), Typeface.BOLD);
                } else if (i == DAY_IN_MONTH + 2) {
                    status_tvs[0][i].setText(R.string.total_A);
                    status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(), Typeface.BOLD);
                } else if (i == DAY_IN_MONTH + 3) {
                    status_tvs[0][i].setText(R.string.total_L);
                    status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(), Typeface.BOLD);
                } else if (i == DAY_IN_MONTH + 4) {
                    status_tvs[0][i].setText(R.string.p_percentage);
                    status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(), Typeface.BOLD);
                }
            }


            for (int i = 1; i < rowSize; i++) {
                roll_tvs[i].setText(String.valueOf(rollArray[i - 1]));
                name_tvs[i].setText(nameArray[i - 1]);

                total_L = 0;
                total_A = 0;
                total_P = 0;

                for (int j = 1; j <= DAY_IN_MONTH + 5; j++) {
                    if (j <= DAY_IN_MONTH) {
                        String day = String.valueOf(j);
                        if (day.length() == 1) day = "0" + day;
                        int d = Integer.parseInt(day);
                        int mnth = getMonth(fullDate);
                        List<DailyRecord> l = getStatusList(studentList.get(i - 1).getName());
                        int rn = studentList.get(i - 1).getRollNo();
                        String name = studentList.get(i - 1).getName();
                        String status = findStatusByNameDate(d, mnth, rn, name, l);
                        studentStatusTableCounter(status);
                        if (status.isEmpty() && j != DAY_IN_MONTH + 1)
                            status_tvs[i][j].setText("H");
                        else status_tvs[i][j].setText(status);
                    }


                    if (j == DAY_IN_MONTH + 1) {
                        status_tvs[i][j].setText(String.valueOf(total_P));
                        status_tvs[0][j].setPadding(16, 16, 16, 16);
                        status_tvs[0][j].setGravity(Gravity.CENTER_HORIZONTAL);
                    } else if (j == DAY_IN_MONTH + 2) {
                        status_tvs[i][j].setText(String.valueOf(total_A));
                        status_tvs[0][j].setPadding(16, 16, 16, 16);
                        status_tvs[0][j].setGravity(Gravity.CENTER_HORIZONTAL);
                    } else if (j == DAY_IN_MONTH + 3) {
                        status_tvs[i][j].setText(String.valueOf(total_L));
                        status_tvs[0][j].setPadding(16, 16, 16, 16);
                        status_tvs[0][j].setGravity(Gravity.CENTER_HORIZONTAL);
                    } else if (j == DAY_IN_MONTH + 4) {
                        status_tvs[i][j].setText(String.valueOf(p_percentageCount));
                        status_tvs[0][j].setPadding(16, 16, 16, 16);
                        status_tvs[0][j].setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                }
            }

            for (int i = 0; i < rowSize; i++) {
                rows[i] = new TableRow(this);

                if (i % 2 == 0) rows[i].setBackgroundColor(Color.parseColor("#EEEEEE"));
                else rows[i].setBackgroundColor(Color.parseColor("#E4E4E4"));

                roll_tvs[i].setPadding(16, 16, 16, 16);
                roll_tvs[i].setGravity(Gravity.CENTER);
                name_tvs[i].setPadding(16, 16, 16, 16);
                name_tvs[i].setGravity(Gravity.CENTER);

                rows[i].addView(roll_tvs[i]);
                rows[i].addView(name_tvs[i]);

                for (int j = 1; j <= DAY_IN_MONTH + 4; j++) {
                    setStatusBackground(status_tvs[i][j]);
                    status_tvs[i][j].setPadding(16, 16, 16, 16);
                    rows[i].addView(status_tvs[i][j]);
                }

                report_progressBar.setVisibility(View.GONE);
                ScrollView sheet_scrollView = findViewById(R.id.sheet_scrollView);
                sheet_scrollView.setVisibility(View.VISIBLE);
                tableLayout.addView(rows[i]);
            }

        } catch (Exception e) {
            Log.d(TAG, "showTable: error = " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setStatusBackground(TextView textView) {
        String txt = textView.getText().toString();
        if (txt.equals("A")) textView.setBackgroundColor(Color.parseColor("#FABFBC"));
        else if (txt.equals("H")) textView.setBackgroundColor(Color.parseColor("#FF18FFFF"));
    }

    private int getNumberOfDayInMonth(int month) {
        int DAY_IN_MONTH;
        switch (month) {
            case 1:
            case 12:
            case 10:
            case 8:
            case 3:
            case 7:
            case 5:
                DAY_IN_MONTH = 31;
                break;
            case 2:
                DAY_IN_MONTH = 28;
                break;
            case 4:
            case 11:
            case 9:
            case 6:
                DAY_IN_MONTH = 30;
                break;
            default:
                DAY_IN_MONTH = 0;
        }
        return DAY_IN_MONTH;
    }

    private List<DailyRecord> getStatusList(String name) {
        List<DailyRecord> studentRecordList = new ArrayList<>();

        try {

            for (DailyAttendance d : attendanceList) {
                if (d.getStudentName().equals(name)) {
                    String nm = d.getStudentName();
                    int rn = d.getStudentRollNo();
                    int day = getDay(d.getAttendanceDate());
                    int month = getMonth(d.getAttendanceDate());
                    String status = mStatus(d.getStudentStatus());

                    DailyRecord dailyRecord =
                            new DailyRecord(nm, rn, day, month, status);
                    studentRecordList.add(dailyRecord);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentRecordList;
    }

    private String findStatusByNameDate(int day, int month, int rn, String name, List<DailyRecord> dList) {

        String s = "";
        for (DailyRecord d : dList) {
            if (day == d.getDay() && month == d.getMonth()
                    && name.equals(d.getcName()) && rn == d.getsRN()) {
                s = d.getStatus();

            }
        }

        return s;
    }

    private String mStatus(String status) {
        String stts = "";
        switch (status) {
            case "Present":
            case "Late":
                stts = "P";
                break;
            case "Absent":
                stts = "A";
                break;
            case "Leave":
                stts = "L";
                break;
        }
        return stts;
    }

    private int getMonth(String date) {
        String d = date.substring(3, 6);
        int i = 0;
        switch (d) {
            case "Jan":
                i = 1;
                break;

            case "Feb":
                i = 2;
                break;

            case "Mar":
                i = 3;
                break;

            case "Apr":
                i = 4;
                break;

            case "May":
                i = 5;
                break;

            case "Jun":
                i = 6;
                break;

            case "Jul":
                i = 7;
                break;

            case "Aug":
                i = 8;
                break;

            case "Sep":
                i = 9;
                break;

            case "Oct":
                i = 10;
                break;

            case "Nov":
                i = 11;
                break;

            case "Dec":
                i = 12;
                break;
        }

        return i;
    }

    private int getYear(String date) {
        Date d = null;
        try {
            d = new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        if (d != null)
            c.setTime(d);
        return c.get(Calendar.YEAR);
    }

    private int getDay(String date) {
        String s = date.substring(0, 2);
        return Integer.parseInt(s);
    }

    private Bitmap getPhotoFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(totalWeight, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Drawable bgdrawable = view.getBackground();

        if (bgdrawable != null) {
            bgdrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return bitmap;
    }

    ActivityResultLauncher<Intent> activityCreatePDFResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    assert intent != null;
                    Uri uri = intent.getData();


                    PdfDocument pdfDocument = new PdfDocument();
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitMapp.getWidth(), bitMapp.getHeight(), 1).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                    Canvas canvas = page.getCanvas();

                    Paint paint = new Paint();
                    paint.setColor(Color.parseColor("#ffffff"));
                    canvas.drawPaint(paint);


                    Bitmap bitmap = Bitmap.createScaledBitmap(this.bitMapp, this.bitMapp.getWidth(), this.bitMapp.getHeight(), true);
                    paint.setColor(Color.BLUE);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    pdfDocument.finishPage(page);
                    File filepath = new File(uri.getPath());

                    try {
                        FileOutputStream outputStream = (FileOutputStream) getContentResolver().openOutputStream(uri);
                        pdfDocument.writeTo(outputStream);
                        outputStream.close();
                        Toast.makeText(this, "Successfully saved", Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    pdfDocument.close();

                    if (myPath.exists()) {
                        myPath.delete();
                    }

                    report_progressBar.setVisibility(View.GONE);

                }
            });

    private void takScreenShot() {

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/");
        if (!folder.exists()) {
            boolean success = folder.mkdir();
        }
        path = folder.getAbsolutePath();
        path = path + "/" + fileName + System.currentTimeMillis() + ".pdf";

        sheetView = findViewById(R.id.sheet_scrollView);
        HorizontalScrollView z = findViewById(R.id.horizontalScrolView);
        totalHeight = z.getChildAt(0).getHeight();
        totalWeight = z.getChildAt(0).getWidth();

        String p = this.getExternalFilesDir("Photo").getAbsolutePath();
        File f = new File(p);
        if (!f.exists()) f.mkdirs();
        File screenShotPath = new File(p, "photo." + System.currentTimeMillis() + ".jpg");
        myPath = new File(screenShotPath.getPath());
        imgURi = myPath.getPath();
        bitMapp = getPhotoFromView(z);


        try {
            FileOutputStream fileOutputStream = new FileOutputStream(screenShotPath);
            bitMapp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        createPdf();
    }


    private void createPdf() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fileName = fileName + System.currentTimeMillis() + ".pdf";

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE, fileName);

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when your app creates the document.
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, "");
            activityCreatePDFResultLauncher.launch(intent);

        } else {


            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/");
            if (!folder.exists()) {
                boolean success = folder.mkdir();
            }
            path = folder.getAbsolutePath();
            path = path + "/" + fileName + System.currentTimeMillis() + ".pdf";


            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitMapp.getWidth(), bitMapp.getHeight(), 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas canvas = page.getCanvas();

            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawPaint(paint);


            Bitmap bitmap = Bitmap.createScaledBitmap(this.bitMapp, this.bitMapp.getWidth(), this.bitMapp.getHeight(), true);
            paint.setColor(Color.BLUE);
            canvas.drawBitmap(bitmap, 0, 0, null);
            pdfDocument.finishPage(page);
            File filepath = new File(path);

            try {
                pdfDocument.writeTo(new FileOutputStream(filepath));
                Toast.makeText(this, "Successfully saved in Documents", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            pdfDocument.close();

            if (myPath.exists()) {
                myPath.delete();
            }

            report_progressBar.setVisibility(View.GONE);

        }
    }

    private void openPdf() {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        Uri data = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        target.setDataAndType(data, "application/pdf");
        target.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Apps to read PDF File", Toast.LENGTH_SHORT).show();
        }

    }

    //ShareFile Method:
    private void shareReport() {

        String stringFile = Environment.getExternalStorageDirectory().getPath() + File.separator;

        File file = new File(stringFile);

        if (!file.exists()) {
            Toast.makeText(this, "File doesn't exists", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("application/pdf");
        intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileName + file));

        startActivity(Intent.createChooser(intentShare, "Share the file . . ."));

    }

    ActivityResultLauncher<Intent> activityCreateExcelResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    assert intent != null;
                    Uri uri = intent.getData();

                    HSSFWorkbook workbook = new HSSFWorkbook();
                    HSSFSheet sheet = workbook.createSheet();
                    int rollCell = 0;
                    int nameCell = 1;


                    for (int i = 0; i < studentList.size() + 2; i++) {
                        sheet.createRow(i);
                        if (sheet.getFirstRowNum() == 0) {
                            sheet.getRow(0).createCell(2).setCellValue(className + " AttendanceSheet for the month of " + month);
                        }
                        if (i == 1) {
                            sheet.getRow(1).createCell(rollCell).setCellValue("Roll No");
                            sheet.getRow(1).createCell(nameCell).setCellValue("Name");
                            for (int j = 2; j <= DAY_IN_MONTH + 1; j++) {
                                sheet.getRow(1).createCell(j).setCellValue(j - 1);
                                if (DAY_IN_MONTH == j) {
                                    sheet.getRow(1).createCell(DAY_IN_MONTH + 2).setCellValue("Total P");
                                    sheet.getRow(1).createCell(DAY_IN_MONTH + 3).setCellValue("Total A");
                                    sheet.getRow(1).createCell(DAY_IN_MONTH + 4).setCellValue("Total L");
                                    sheet.getRow(1).createCell(DAY_IN_MONTH + 5).setCellValue("Present %");
                                }
                            }
                        }
                    }
                    for (int i = 2; i <= studentList.size() + 1; i++) {
                        sheet.getRow(i).createCell(rollCell).setCellValue(rollArray[i - 2]);
                        sheet.getRow(i).createCell(nameCell).setCellValue(nameArray[i - 2]);
                        total_P = 0;
                        total_A = 0;
                        total_L = 0;
                        p_percentageCount = 0;
                        for (int j = 2; j <= DAY_IN_MONTH + 1; j++) {
                            String day = String.valueOf(j - 1);
                            if (day.length() == 1) day = "0" + day;
                            int d = Integer.parseInt(day);
                            int mnth = getMonth(fullDate);
                            List<DailyRecord> l = getStatusList(studentList.get(i - 2).getName());
                            int rn = studentList.get(i - 2).getRollNo();
                            String name = studentList.get(i - 2).getName();
                            String status = findStatusByNameDate(d, mnth, rn, name, l);

                            if (status.isEmpty()) {
                                sheet.getRow(i).createCell(j).setCellValue("H");
                            } else {
                                sheet.getRow(i).createCell(j).setCellValue(status);
                                countStudentStatus(status);
                            }

                            if (j == DAY_IN_MONTH + 1) {
                                sheet.getRow(i).createCell(DAY_IN_MONTH + 2).setCellValue(total_P);
                                sheet.getRow(i).createCell(DAY_IN_MONTH + 3).setCellValue(total_A);
                                sheet.getRow(i).createCell(DAY_IN_MONTH + 4).setCellValue(total_L);
                                sheet.getRow(i).createCell(DAY_IN_MONTH + 5).setCellValue(p_percentageCount);
                            }
                        }
                        total_P = 0;
                        total_A = 0;
                        total_L = 0;
                        p_percentageCount = 0;
                    }

                    try {

                        File file = new File(uri.getPath());
                        if (!file.exists()) {
                            file.mkdirs();
                            Log.d(TAG, "filee created: " + file.getName());
                        } else {
                            Log.d(TAG, "filee already creaaated: " + file.getName());

                        }
                        FileOutputStream outputStream = (FileOutputStream) getContentResolver().openOutputStream(uri);
                        workbook.write(outputStream);
                        outputStream.close();
                        Toast.makeText(this, "Successfully saved", Toast.LENGTH_LONG).show();
                    } catch (IOException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                        Log.d(TAG, "createExcelSheet: Error: " + fileNotFoundException.getMessage());
                        Toast.makeText(this, "Creating Excel Sheet has been failed", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    private void createExcelSheet() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fileName = fileName + System.currentTimeMillis() + ".xls";

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.ms-excel");
            intent.putExtra(Intent.EXTRA_TITLE, fileName);

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when your app creates the document.
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, "");
            activityCreateExcelResultLauncher.launch(intent);

        } else {
            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/");
            if (!folder.exists()) {
                boolean success = folder.mkdir();
            }
            path = folder.getAbsolutePath();
            path = fileName + System.currentTimeMillis() + ".xls";

            File patha = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS);
            if (!patha.exists()) {
                patha.mkdir();
            }

            File file = new File(patha, path);

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            int rollCell = 0;
            int nameCell = 1;


            Log.d(TAG, "excel" + studentList.get(studentList.size() - 1));
            for (int i = 0; i < studentList.size() + 2; i++) {

                sheet.createRow(i);
                if (sheet.getFirstRowNum() == 0) {
                    sheet.getRow(0).createCell(2).setCellValue(className + " AttendanceSheet for the month of " + month);
                }
                if (i == 1) {
                    sheet.getRow(1).createCell(rollCell).setCellValue("Roll No");
                    sheet.getRow(1).createCell(nameCell).setCellValue("Name");
                    for (int j = 2; j <= DAY_IN_MONTH + 1; j++) {
                        sheet.getRow(1).createCell(j).setCellValue(j - 1);
                        if (DAY_IN_MONTH == j) {
                            sheet.getRow(1).createCell(DAY_IN_MONTH + 2).setCellValue("Total P");
                            sheet.getRow(1).createCell(DAY_IN_MONTH + 3).setCellValue("Total A");
                            sheet.getRow(1).createCell(DAY_IN_MONTH + 4).setCellValue("Total L");
                            sheet.getRow(1).createCell(DAY_IN_MONTH + 5).setCellValue("Present %");
                        }
                    }
                }
            }
            for (int i = 2; i <= studentList.size(); i++) {
                sheet.getRow(i).createCell(rollCell).setCellValue(rollArray[i - 2]);
                sheet.getRow(i).createCell(nameCell).setCellValue(nameArray[i - 2]);
                for (int j = 2; j <= DAY_IN_MONTH + 1; j++) {
                    String day = String.valueOf(j - 1);
                    if (day.length() == 1) day = "0" + day;
                    int d = Integer.parseInt(day);
                    int mnth = getMonth(fullDate);
                    List<DailyRecord> l = getStatusList(studentList.get(i - 2).getName());
                    int rn = studentList.get(i - 2).getRollNo();
                    String name = studentList.get(i - 2).getName();
                    String status = findStatusByNameDate(d, mnth, rn, name, l);
                    if (status.isEmpty()) sheet.getRow(i).createCell(j).setCellValue("H");
                    else {
                        sheet.getRow(i).createCell(j).setCellValue(status);
                        countStudentStatus(status);
                    }
                    if (j == DAY_IN_MONTH + 1) {
                        sheet.getRow(i).createCell(DAY_IN_MONTH + 2).setCellValue(total_P);
                        sheet.getRow(i).createCell(DAY_IN_MONTH + 3).setCellValue(total_A);
                        sheet.getRow(i).createCell(DAY_IN_MONTH + 4).setCellValue(total_L);
                        sheet.getRow(i).createCell(DAY_IN_MONTH + 5).setCellValue(p_percentageCount);
                    }
                }
                total_P = 0;
                total_A = 0;
                total_L = 0;
                p_percentageCount = 0;
            }


            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                workbook.write(out);
                out.close();
                Toast.makeText(this, "Successfully saved in Documents", Toast.LENGTH_LONG).show();
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
                Log.d(TAG, "createExcelSheet: Error: " + fileNotFoundException.getMessage());
                Toast.makeText(this, "Creating Excel Sheet has been failed", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void countStudentStatus(String status) {
        switch (status) {
            case "A":
                total_A++;
                break;

            case "P":
                total_P++;
                break;

            case "L":
                total_L++;
                break;
        }

        p_percentageCount = total_P * 100 / (total_P + total_A + total_L);
    }

    private void studentStatusTableCounter(String status) {
        p_percentageCount = 0;

        switch (status) {
            case "A":
                total_A++;
                break;

            case "P":
                total_P++;
                break;

            case "L":
                total_L++;
                break;
        }

        int c = (total_P + total_A + total_L);
        if (c != 0)
            p_percentageCount = total_P * 100 / c;
    }

    //Check User Permission for import EXCEL LIST:
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
            return EasyPermissions.hasPermissions(this, perms);
        } else {

            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            boolean chek = result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
            Log.d(TAG, "checkPermissions: " + chek);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    //Request User Permission for import EXCEL LIST:
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", this.getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    11);
        }
    }

    //alert Dialog:
    private void saveReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Want to save as?");
        builder.setPositiveButton("PDF", (dialog, which) ->
        {
            report_progressBar.setVisibility(View.VISIBLE);
            takScreenShot();
            prog_tv.setText("Generating PDF. . .");

            //AdMob InterstitialAd:
            if (AdManager.mInterstitialAd != null) {
                AdManager.mInterstitialAd.show(MonthlyAttendanceActivity.this);

                AdManager.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                        AdManager.mInterstitialAd = null;
                        AdManager.loadInterstitial(MonthlyAttendanceActivity.this);
                    }
                });
            }
        });
        builder.setNegativeButton("EXCEL", (dialog, which) -> {
            createExcelSheet();

            //AdMob InterstitialAd:
            if (AdManager.mInterstitialAd != null) {
                AdManager.mInterstitialAd.show(MonthlyAttendanceActivity.this);

                AdManager.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                        AdManager.mInterstitialAd = null;
                        AdManager.loadInterstitial(MonthlyAttendanceActivity.this);
                    }
                });
            }
        });
        builder.show();
    }

}