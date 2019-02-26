package com.Demo.onekey2alarm;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.Phones;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class contacts extends Activity{
	private Button bt_Add;
	private EditText NumText;
	static String number,username,item_name="",item_num="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		/*
		 * 第二个页面什么时候给第一个页面回传数据
		 * 回传到第一个页面的实际上是一个Intent对象
		 */
		bt_Add = (Button) findViewById(R.id.bt_Add);
		NumText = (EditText) findViewById(R.id.et_name);
		bt_Add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent data = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(data, 0);
				
			}
		});
		
	}
	//获取选择的号码：
		protected void onActivityResult (int requestCode, int resultCode, Intent data) 
		{
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == Activity.RESULT_OK) {
	            ContentResolver reContentResolverol = getContentResolver();
	             Uri contactData = data.getData();
	             @SuppressWarnings("deprecation")
	            Cursor cursor = managedQuery(contactData, null, null, null, null);
	             cursor.moveToFirst();
	             username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
	            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
	                     null, 
	                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, 
	                     null, 
	                     null);
	             while (phone.moveToNext()) {
	                 number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	                 item_name = item_name+username+"; ";
	                 item_num = number+" "+item_num;
	                 NumText.setText(item_name);
	   
	             }

	         }
		}
		
}

		
		