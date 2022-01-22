package com.example.valarmorghulis.firebaseauth;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.EventLogTags;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.TimePickerDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

import java.util.Calendar;

public class SellFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_CAMERA_REQUEST = 0;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private Button mButtonTimePicker;
    private Button mButtonCloseTimePicker;
    private EditText mEditTextFileName;
    private EditText mEditTextFilePrice;
    private EditText mEditTextOpenTime;
    private EditText mEditTextCloseTime;
    private int mHour, mMinute;
    private ImageView mImageView;
    private TextView mDescription;
    private EditText mLocation;
    private Spinner spinnerCategory;
    private String selectedCategory;
    private EditText location1, location2, location3, location4, location5,
            deliverycharge1,deliveryCharge2, deliveryCharge3, deliveryCharge4, deliveryCharge5;
    Switch delivery;
    TextView location, textViewDelivery;
    RadioGroup radioGroup;
    RadioButton payBill, tillNo;
    EditText  editPayBill,editTillNo, editPhoneNo;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    FirebaseAuth mAuth;
    Uri uri;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sell, container, false);

        mButtonChooseImage = v.findViewById(R.id.button_choose_image);
        mButtonUpload = v.findViewById(R.id.button_upload);
        mButtonTimePicker = v.findViewById(R.id.pick_time_button);
        mButtonCloseTimePicker = v.findViewById(R.id.pick_close_time_button);
        mEditTextFileName = v.findViewById(R.id.edit_text_file_name);
        mEditTextFilePrice = v.findViewById(R.id.edit_text_file_price);
        mEditTextOpenTime = v.findViewById(R.id.open_time);
        mEditTextCloseTime = v.findViewById(R.id.close_time);
        mImageView = v.findViewById(R.id.image_view);
        mProgressBar = v.findViewById(R.id.progress_bar);
        mDescription = v.findViewById(R.id.Description);
        mLocation = v.findViewById(R.id.Location);
        spinnerCategory = v.findViewById(R.id.spinner_categories);
        deliverycharge1 = v.findViewById(R.id.textViewCharge1);
        deliveryCharge2 = v.findViewById(R.id.textViewCharge2);
        deliveryCharge3 = v.findViewById(R.id.textViewCharge3);
        deliveryCharge4 = v.findViewById(R.id.textViewCharge4);
        deliveryCharge5 = v.findViewById(R.id.textViewCharge5);
        location1 = v.findViewById(R.id.text_view_location);
        location2 = v.findViewById(R.id.text_view_location2);
        location3 = v.findViewById(R.id.text_view_location3);
        location4 = v.findViewById(R.id.text_view_location4);
        location5 = v.findViewById(R.id.text_view_location5);
        location = v.findViewById(R.id.location_tag);
        radioGroup = v.findViewById(R.id.groupradio);
        payBill = v.findViewById(R.id.rb_pay_bill);
        tillNo = v.findViewById(R.id.rb_till_no);
        editPayBill = v.findViewById(R.id.edit_text_pay_bill);
        editTillNo = v.findViewById(R.id.edit_text_till_no);
        editPhoneNo = v.findViewById(R.id.edit_text_phone_no);
        textViewDelivery = v.findViewById(R.id.delivery_tag);
        delivery = (Switch) v.findViewById(R.id.SwitchButton);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(getActivity(), R.array.categories, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory =parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), selectedCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        radioGroup.clearCheck();

        radioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        switch(selectedId) {
                            case R.id.rb_pay_bill:
                                editPayBill.setVisibility(View.VISIBLE);
                                editTillNo.setVisibility(View.GONE);
                                editPhoneNo.setVisibility(View.GONE);
                                if (editPayBill.getText().toString().trim().isEmpty()) {
                                    editPayBill.setError("Paybill required");
                                    editPayBill.requestFocus();
                                    return;
                                }
                                break;
                            case R.id.rb_till_no:
                                editTillNo.setVisibility(View.VISIBLE);
                                editPayBill.setVisibility(View.GONE);
                                editPhoneNo.setVisibility(View.GONE);
                                if (editTillNo.getText().toString().trim().isEmpty()) {
                                    editTillNo.setError("Till number required");
                                    editTillNo.requestFocus();
                                    return;
                                }
                                break;
                            case R.id.rb_phone_no:
                                editPhoneNo.setVisibility(View.VISIBLE);
                                editPayBill.setVisibility(View.GONE);
                                editTillNo.setVisibility(View.GONE);
                                if (editPhoneNo.getText().toString().trim().isEmpty()) {
                                    editPhoneNo.setError("Phone number required");
                                    editPhoneNo.requestFocus();
                                    return;
                                }
                                break;
                        }

                    }
                });

        delivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    location.setVisibility(View.VISIBLE);
                    location1.setVisibility(View.VISIBLE);
                    location2.setVisibility(View.VISIBLE);
                    location3.setVisibility(View.VISIBLE);
                    location4.setVisibility(View.VISIBLE);
                    location5.setVisibility(View.VISIBLE);
                    deliverycharge1.setVisibility(View.VISIBLE);
                    deliveryCharge2.setVisibility(View.VISIBLE);
                    deliveryCharge3.setVisibility(View.VISIBLE);
                    deliveryCharge4.setVisibility(View.VISIBLE);
                    deliveryCharge5.setVisibility(View.VISIBLE);

                    if (location1.getText().toString().trim().isEmpty()) {
                        location1.setError("Location required");
                        location1.requestFocus();
                        return;
                    }
                    if (deliverycharge1.getText().toString().trim().isEmpty()) {
                        deliverycharge1.setError("Amount required");
                        deliverycharge1.requestFocus();
                        return;
                    }
                }
                else {
                    location.setVisibility(View.GONE);
                    location1.setVisibility(View.GONE);
                    location2.setVisibility(View.GONE);
                    location3.setVisibility(View.GONE);
                    location4.setVisibility(View.GONE);
                    location5.setVisibility(View.GONE);
                    deliverycharge1.setVisibility(View.GONE);
                    deliveryCharge2.setVisibility(View.GONE);
                    deliveryCharge3.setVisibility(View.GONE);
                    deliveryCharge4.setVisibility(View.GONE);
                    deliveryCharge5.setVisibility(View.GONE);
                }

            }
        });

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeChooser();
            }
        });
        mButtonCloseTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCloseTimeChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }


            }
        });


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        location.setVisibility(View.GONE);
        location1.setVisibility(View.GONE);
        location2.setVisibility(View.GONE);
        location3.setVisibility(View.GONE);
        location4.setVisibility(View.GONE);
        location5.setVisibility(View.GONE);
        deliverycharge1.setVisibility(View.GONE);
        deliveryCharge2.setVisibility(View.GONE);
        deliveryCharge3.setVisibility(View.GONE);
        deliveryCharge4.setVisibility(View.GONE);
        deliveryCharge5.setVisibility(View.GONE);
        editPayBill.setVisibility(View.GONE);
        editTillNo.setVisibility(View.GONE);
        editPhoneNo.setVisibility(View.GONE);

        NetworkConnection networkConnection = new NetworkConnection();
        if (networkConnection.isConnectedToInternet(getActivity())
                || networkConnection.isConnectedToMobileNetwork(getActivity())
                || networkConnection.isConnectedToWifi(getActivity())) {

        } else {
            networkConnection.showNoInternetAvailableErrorDialog(getActivity());
            return;
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openTimeChooser() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        mEditTextOpenTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void openCloseTimeChooser() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        mEditTextCloseTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            //cropImage();

            Picasso.with(getActivity()).load(mImageUri).into(mImageView);
        }
    }

    private void cropImage() {
        try {
            Intent cropIntent;

            cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(mImageUri, "image/*");

            cropIntent.putExtra("crop", " true");
            cropIntent.putExtra("outputx", 180);
            cropIntent.putExtra("outputY", 180);
            cropIntent.putExtra("aspectx", 3);
            cropIntent.putExtra("aspecty", 4);
            cropIntent.putExtra("scaleUpIfNeeded", true);
            cropIntent.putExtra("return-data ", true);

            startActivityForResult(cropIntent, 1);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mAuth.getInstance().getCurrentUser().getDisplayName() == null) {
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("seller", 1);
            profileFragment.setArguments(bundle);
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction().replace(R.id.frag_container, profileFragment)
                    .commit();

            return;
        }
        if (mEditTextFileName.getText().toString().trim().isEmpty()) {
            mEditTextFileName.setError("Name required");
            mEditTextFileName.requestFocus();
            return;
        }

        if (mEditTextFilePrice.getText().toString().trim().isEmpty()) {
            mEditTextFilePrice.setError("Price required");
            mEditTextFilePrice.requestFocus();
            return;
        }

        if (mLocation.getText().toString().trim().isEmpty()) {
            mLocation.setError("Location required");
            mLocation.requestFocus();
            return;
        }

        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(getActivity(), "No payment method has been selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            mImageUri = null;
                            mImageView.setImageBitmap(null);
                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();

                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
                                            uri.toString(), mEditTextFilePrice.getText().toString().trim(),
                                            mDescription.getText().toString().trim(),
                                            mLocation.getText().toString().trim(),
                                            mEditTextOpenTime.getText().toString(),
                                            mEditTextCloseTime.getText().toString(),
                                            selectedCategory.toString(),
                                            location1.getText().toString(),
                                            deliverycharge1.getText().toString(),
                                            location2.getText().toString(),
                                            deliveryCharge2.getText().toString(),
                                            location3.getText().toString(),
                                            deliveryCharge3.getText().toString(),
                                            location4.getText().toString(),
                                            deliveryCharge4.getText().toString(),
                                            location5.getText().toString(),
                                            deliveryCharge5.getText().toString(),
                                            editPayBill.getText().toString(),
                                            editTillNo.getText().toString(),
                                            editPhoneNo.getText().toString());
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                    mEditTextFileName.setText("");
                                    mEditTextFilePrice.setText("");
                                    mDescription.setText("");
                                    mLocation.setText("");
                                    mEditTextOpenTime.setText("");
                                    mEditTextCloseTime.setText("");
                                    location1.setText("");
                                    deliverycharge1.setText("");
                                    location2.setText("");
                                    deliveryCharge2.setText("");
                                    location3.setText("");
                                    deliveryCharge3.setText("");
                                    location4.setText("");
                                    deliveryCharge4.setText("");
                                    location5.setText("");
                                    deliveryCharge5.setText("");
                                    editPayBill.setText("");
                                    editTillNo.setText("");
                                    editPhoneNo.setText("");
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }

    }


}
