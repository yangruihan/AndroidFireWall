package com.yrh.firewall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.yrh.firewall.R;
import com.yrh.firewall.model.PhoneInfo;
import com.yrh.firewall.utils.DBOpenHelper;

/**
 * Created by Yrh on 2015/12/14.
 */
public class AddPhoneNumDialogActivity extends AppCompatActivity {

    private EditText mEditText;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private CheckBox mCbBoth;

    private Intent intent;

    private DBOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_add_phone_num);

        mDbOpenHelper = new DBOpenHelper(this, "db", null, DBOpenHelper.DB_VERSION);

        mEditText = (EditText) findViewById(R.id.etAddPhone);
        mBtnCancel = (Button) findViewById(R.id.btnAddPhoneCancel);
        mBtnConfirm = (Button) findViewById(R.id.btnAddPhoneConfirm);
        mCbBoth = (CheckBox) findViewById(R.id.cbAddPhoneBoth);

        intent = getIntent();

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = mEditText.getText().toString().trim();

                if (phoneNum.length() == 11) {
                    try {
                        Long.parseLong(phoneNum);

                        if (mCbBoth.isChecked()) {
                            PhoneInfo phoneInfo = new PhoneInfo(phoneNum, 1, 1);
                            mDbOpenHelper.addPhoneInfo(phoneInfo);

                            Bundle b = new Bundle();
                            b.putString("phoneNum", phoneNum);
                            b.putInt("inIncommingBlack", 1);
                            b.putInt("inSMSBlack", 1);
                            intent.putExtras(b);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else if (intent.getFlags() == MainActivity.INTENT_FLAG_INCOMMING) {


                            PhoneInfo phoneInfo = new PhoneInfo(phoneNum, 1, 0);
                            mDbOpenHelper.addPhoneInfo(phoneInfo);

                            Bundle b = new Bundle();
                            b.putString("phoneNum", phoneNum);
                            b.putInt("inIncommingBlack", 1);
                            b.putInt("inSMSBlack", 0);
                            intent.putExtras(b);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else if (intent.getFlags() == MainActivity.INTENT_FLAG_SMS) {

                            PhoneInfo phoneInfo = new PhoneInfo(phoneNum, 0, 1);
                            mDbOpenHelper.addPhoneInfo(phoneInfo);

                            Bundle b = new Bundle();
                            b.putString("phoneNum", phoneNum);
                            b.putInt("inIncommingBlack", 0);
                            b.putInt("inSMSBlack", 1);
                            intent.putExtras(b);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        Snackbar snackbar = Snackbar.make(v, "输入有误，请重新输入", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        return;
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(v, "输入有误，请重新输入", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    return;
                }
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}
