package org.techtown.yeminapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FinedustFragment extends Fragment{
    int grade = 0;
    int grade2 = 0;
    int[] images = {R.drawable.good, R.drawable.normal, R.drawable.bad, R.drawable.verybad};

    EditText edit;
    TextView dateTextView, text;
    String data;
    ImageView imageView;
    InputMethodManager imm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_finedust, container, false);

        edit = rootView.findViewById(R.id.edit);
        text = rootView.findViewById(R.id.result);
        imageView = rootView.findViewById(R.id.imageView5);
        imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);

        dateTextView=rootView.findViewById(R.id.textView9);
        long nowDate = System.currentTimeMillis();
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy.MM.dd hh시");
        String getTime = sDate.format(nowDate);
        dateTextView.append(getTime);

        Button button = rootView.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClick(v);
                imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
            }
        });

        return rootView;
    }

    //Button을 클릭했을 때 자동으로 호출되는 callback method
    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        data = getXmlData();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text.setText(data);
                            }
                        });
                    }
                }).start();
                break;
        }
    }   //mOnClick method.


    //XmlPullParser를 이용하여 OpenAPI XML 파일 파싱하기
    String getXmlData() {
        StringBuffer buffer = new StringBuffer();
        String key = "DuGy2ql2LMAU%2FxFGN3Fu0OR%2Bk6y4zuoTW7kpEPjLe3IOnqRe0QscARteW3Y55xHY8HESiiRupIKfCAifjf5Rqg%3D%3D";

        String str = edit.getText().toString();
        String location = new String();
        try {
            location = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String queryUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?sidoName="
                            + location + "&pageNo=1&numOfRows=10&ServiceKey="+ key +"&ver=1.3";

        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱 시작\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if(tag.equals("item"));
                        else if(tag.equals("stationName")) {
                            buffer.append("측정소 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag.equals("pm10Value")) {
                            buffer.append("미세먼지 농도 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("㎍/㎥\n");
                        }
                        else if(tag.equals("pm25Value")) {
                            buffer.append("초미세먼지 농도 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("㎍/㎥\n");
                        }
                        else if(tag.equals("pm10Grade")) {
                            buffer.append("미세먼지 등급 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            grade = Integer.parseInt(xpp.getText());
                            switch (grade) {
                                case 1:
                                    buffer.append(" (좋음)");
                                    break;
                                case 2:
                                    buffer.append(" (보통)");
                                    break;
                                case 3:
                                    buffer.append(" (나쁨)");
                                    break;
                                case 4:
                                    buffer.append(" (매우나쁨)");
                                    break;
                            }
                            buffer.append("\n");
                        }
                        else if(tag.equals("pm25Grade")) {
                            buffer.append("초미세먼지 등급 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            grade2 = Integer.parseInt(xpp.getText());
                            switch (grade2) {
                                case 1:
                                    buffer.append(" (좋음)");
                                    break;
                                case 2:
                                    buffer.append(" (보통)");
                                    break;
                                case 3:
                                    buffer.append(" (나쁨)");
                                    break;
                                case 4:
                                    buffer.append(" (매우나쁨)");
                                    break;
                            }
                            buffer.append("\n");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if(tag.equals("item"))
                            buffer.append("\n");
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }

        switch (grade) {
            case 1:
                imageView.setImageResource(images[0]);
                break;
            case 2:
                imageView.setImageResource(images[1]);
                break;
            case 3:
                imageView.setImageResource(images[2]);
                break;
            case 4:
                imageView.setImageResource(images[3]);
        }

        return buffer.toString();
    }
}