package vn.binhnc.manager.activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.binhnc.manager.R;
import vn.binhnc.manager.constant.GlobalFuntion;
import vn.binhnc.manager.databinding.ActivityThemSpactivityBinding;
import vn.binhnc.manager.model.KeyValueItem;
import vn.binhnc.manager.model.SanPham;
import vn.binhnc.manager.model.TheLoai;
import vn.binhnc.manager.model.ThemSPModel;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.utils.NetWorkChangeListener;
import vn.binhnc.manager.utils.Utils;

public class ThemSPActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private int loai = 0;
    private ActivityThemSpactivityBinding binding;
    private ApiBanHang apiBanHang;
    private List<TheLoai> mListTheLoai;
    private ArrayList<KeyValueItem> dataListSpinner;
    private CompositeDisposable compositeDisposable;
    private String mediaPath;
    private SanPham sanPhamSua;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemSpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        mListTheLoai = new ArrayList<>();
        dataListSpinner = new ArrayList<>();
        //=>
        initDataSpinner();
        initControl();
        initToolBar();

        //=>
        Intent intent = getIntent();
        sanPhamSua = (SanPham) intent.getSerializableExtra("sua");
        if (sanPhamSua == null) {
            // Thêm mới
            flag = false;
        } else {
            // Sửa
            flag = true;
            binding.toolbarManageProduct.setTitle("Sửa sản phẩm");
            binding.btnthem.setText("Sửa sản phẩm");
            // show data
            binding.tensp.setText(sanPhamSua.getTensp());
            String strGia = sanPhamSua.getGiasp() + "";
            binding.giasp.setText(strGia);
            binding.hinhanh.setText(sanPhamSua.getHinhanh());
            binding.mota.setText(sanPhamSua.getMota());
            String strSlsp = sanPhamSua.getSltonkho() + "";
            binding.slsp.setText(strSlsp);
            //=>
            Log.d("loaispsua", "" + sanPhamSua.getLoai());
        }
    }

    private void initToolBar() {
        setSupportActionBar(binding.toolbarManageProduct);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Hiển thị nút quay lại
            actionBar.setDisplayHomeAsUpEnabled(true);
            // Set icon cho nút quay lại
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        }
        binding.toolbarManageProduct.setNavigationOnClickListener(view -> finish());
    }

    private void initControl() {
        binding.spinnerLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KeyValueItem selectedItem = (KeyValueItem) parent.getItemAtPosition(position);
                int selectedKey = selectedItem.getKey();
                String selectedValue = selectedItem.getValue();
                // Xử lý dữ liệu được chọn
                // Ví dụ: hiển thị key và value trong Log
                Log.d("Spinner", "Selected Key: " + selectedKey);
                Log.d("Spinner", "Selected Value: " + selectedValue);
                loai = selectedKey;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.btnthem.setOnClickListener(v -> {
            if (!flag) {
                themsanpham();
            } else {
                suaSanPham();
            }

        });
        // Bắt sự kiện click để tùy chọn upload ảnh
        binding.imgcamera.setOnClickListener(v -> ImagePicker.with(ThemSPActivity.this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            try {
                // Xử lý kết quả trả về từ ImagePicker
                // Uri imageUri = data.getData();
                mediaPath = data.getDataString();
                uploadMultipleFiles();
                Log.d("log", "onActivityResult: " + mediaPath);
            } catch (Exception e) {
                // Xử lý ngoại lệ
                e.printStackTrace();
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            GlobalFuntion.showAlertMessageDialog(this, "Thông báo",
                    "Lỗi tải file", 3000);
        }
    }

    private void suaSanPham() {
        String str_ten = binding.tensp.getText().toString().trim();
        String str_gia = binding.giasp.getText().toString().trim();
        String str_mota = binding.mota.getText().toString().trim();
        String str_hinhanh = binding.hinhanh.getText().toString().trim();
        String str_sl = binding.slsp.getText().toString().trim();
        String title = "Thông báo";
        String mess = "";
        int int_gia = 0;
        int int_sl = 0;
        try {
            int_gia = Integer.parseInt(str_gia);
            int_sl = Integer.parseInt(str_sl);
        } catch (Exception e) {

        }
        if (TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_mota) ||
                TextUtils.isEmpty(str_sl) || TextUtils.isEmpty(str_hinhanh) || loai == 0) {
            mess = "Vui lòng nhập đủ thông tin!";
            GlobalFuntion.showAlertMessageDialog(this, title, mess, 3000);
        } else if (int_gia <= 0) {
            mess = "Giá phải > 0";
            GlobalFuntion.showAlertMessageDialog(this, title, mess, 3000);
        } else if (int_sl <= 0) {
            mess = "Số lượng phải > 0 và < 500";
            GlobalFuntion.showAlertMessageDialog(this, title, mess, 3000);
        } else {
            compositeDisposable.add(apiBanHang.updateSp(str_ten, str_gia, str_hinhanh, str_mota, loai, Integer.parseInt(str_sl), sanPhamSua.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if (messageModel.isSuccess()) {
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    resetField();
                                    Intent quanly = new Intent(getApplicationContext(), QuanLyActivity.class);
                                    startActivity(quanly);
                                } else {
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }, throwable -> {
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                    ));
        }
    }

    private void themsanpham() {
        String str_ten = binding.tensp.getText().toString().trim();
        String str_gia = binding.giasp.getText().toString().trim();
        String str_mota = binding.mota.getText().toString().trim();
        String str_hinhanh = binding.hinhanh.getText().toString().trim();
        String str_sl = binding.slsp.getText().toString();
        String title = "Thông báo";
        String mess = "";
        int int_gia = 0;
        int int_sl = 0;
        try {
            int_gia = Integer.parseInt(str_gia);
            int_sl = Integer.parseInt(str_sl);
        } catch (Exception e) {

        }
        if (TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_sl) || TextUtils.isEmpty(str_mota) || TextUtils.isEmpty(str_hinhanh) || loai == 0) {
            mess = "Vui lòng nhập đủ thông tin!";
            GlobalFuntion.showAlertMessageDialog(this, title, mess, 3000);
        } else if (int_gia <= 0) {
            mess = "Giá phải > 0";
            GlobalFuntion.showAlertMessageDialog(this, title, mess, 3000);
        } else if (int_sl <= 0) {
            mess = "Số lượng phải > 0 và < 500";
            GlobalFuntion.showAlertMessageDialog(this, title, mess, 3000);
        } else {
            compositeDisposable.add(apiBanHang.insertSp(str_ten, str_gia, str_hinhanh, str_mota, loai, Integer.parseInt(str_sl))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if (messageModel.isSuccess()) {
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    resetField();
                                } else {
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }, throwable -> {
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                    ));
        }
    }

    private void resetField() {
        binding.tensp.setText("");
        binding.giasp.setText("");
        binding.slsp.setText("0");
        binding.hinhanh.setText("");
        binding.mota.setText("");
        binding.spinnerLoai.setSelection(0);
    }

    // Lấy đường dẫn chuẩn cho file ảnh
    private String getPath(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    // Uploading Image/Video
    private void uploadMultipleFiles() {
        //
        Uri uri = Uri.parse(mediaPath);
        //
        File file = new File(getPath(uri));
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);

        Call<ThemSPModel> call = apiBanHang.uploadFileProduct(fileToUpload1);
        call.enqueue(new Callback<ThemSPModel>() {
            @Override
            public void onResponse(@NonNull Call<ThemSPModel> call, @NonNull Response<ThemSPModel> response) {
                ThemSPModel serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.isSuccess()) {
                        //Toast.makeText(getApplicationContext(), serverResponse.getMessage(),Toast.LENGTH_SHORT).show();
                        binding.hinhanh.setText(serverResponse.getName());
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.v("Response", serverResponse.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ThemSPModel> call, @NonNull Throwable t) {

            }
        });
    }

    private void initDataSpinner() {
        // Lấy về thể loại sản phẩm
        compositeDisposable.add(apiBanHang.getTheLoai()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        theLoaiModel -> {
                            if (theLoaiModel.isSuccess()) {
                                mListTheLoai = theLoaiModel.getResult();
                                for (TheLoai t : mListTheLoai) {
                                    dataListSpinner.add(new KeyValueItem(t.getId(), t.getTentheloai()));
                                }
                                ArrayAdapter<KeyValueItem> spinnerAdapter = new ArrayAdapter<KeyValueItem>(
                                        ThemSPActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item, dataListSpinner);
                                binding.spinnerLoai.setAdapter(spinnerAdapter);
                                //=> Nếu là sửa sản phẩm thì sẽ đặt selected cho spinner
                                int index = 0;
                                if (flag) {
                                    for (KeyValueItem k : dataListSpinner) {
                                        if (k.getKey() == sanPhamSua.getLoai()) {
                                            index = dataListSpinner.indexOf(k);
                                        }
                                    }
                                    binding.spinnerLoai.setSelection(index);
                                }
                            }
                        }, throwable -> {
                        }
                ));
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(netWorkChangeListener);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}