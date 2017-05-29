package com.example.streetreport;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ShareActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        //액션바 타이틀 변경하기
        getSupportActionBar().setTitle("공유하기");
        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFAFAFA));
        //홈버튼 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //액션버튼 메뉴 액션바에 집어 넣기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    //메뉴 아이템을 클릭했을 때 발생되는 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.menu_complete) {
//            Toast.makeText(this, "공유 완료", Toast.LENGTH_SHORT).show();
//            return true;
//        }
////
//        if( id==R.id.action_home){
//            Toast.makeText(this, "홈으로 이동", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


}

