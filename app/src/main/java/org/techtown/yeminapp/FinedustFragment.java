package org.techtown.yeminapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class FinedustFragment extends Fragment {
    public static interface ImageSelectionCallback {
        public void onImageSelected(int position);
    }

    public ImageSelectionCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof ImageSelectionCallback) {
            callback = (ImageSelectionCallback) context;
        }
    }

    EditText edit;
    TextView text;
    String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_finedust, container, false);

        edit = rootView.findViewById(R.id.edit);
        text = rootView.findViewById(R.id.result);

        Button button = rootView.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClick(v);
            }
        });
        return rootView;
    }

    //Button을 클릭했을 때 자동으로 호출되는 callback method
    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                //Android 4.0 이상부터는 네트워크를 이용할 때 반드시 Thread를 사용해야함
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //XML data를 파싱해서 String 객체로 얻어오기
                        data = getXmlData();
                        //UI Thread(Main Thread)를 제외한 어떤 Thread도 화면을 변경할 수 없기 때문에
                        //runOnUiThread()를 이용하여 UI Thread가 TextView 글씨 변경하도록 함
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Log.d("log msg", data);
                                text.setText(data);     //TextView에 문자열 data 출력
                            }
                        });
                    }
                }).start();
                break;
        }
    }   //mOnClick method.


    //XmlPullParser를 이용하여 OpenAPI XML 파일 파싱하기
    String getXmlData() {
        int grade = 1;
        StringBuffer buffer = new StringBuffer();
        String key = "DuGy2ql2LMAU%2FxFGN3Fu0OR%2Bk6y4zuoTW7kpEPjLe3IOnqRe0QscARteW3Y55xHY8HESiiRupIKfCAifjf5Rqg%3D%3D";

        String str = edit.getText().toString();     //EditText에 작성된 Text 얻어오기
        String location = new String();
        try {
            location = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String queryUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?sidoName="
                            + location + "&pageNo=1&numOfRows=10&ServiceKey="+ key +"&ver=1.3";

        try {
            URL url = new URL(queryUrl);            //문자열로 된 요청 url을 URL 객체로 생성
            InputStream is = url.openStream();      //url 위치로 입력스트림 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));       //inputstream으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱 시작\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();    //태그 이름 얻어오기

                        if(tag != null && tag.equals("item"));     //첫번째 검색결과
                        else if(tag != null && tag.equals("stationName")) {
                            buffer.append("구 이름 : ");
                            xpp.next();
                            buffer.append(xpp.getText());   //title 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("dataTime")) {
                            buffer.append("시간 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("pm10Value")) {
                            buffer.append("미세먼지 농도 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("pm25Value")) {
                            buffer.append("초미세먼지 농도 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("pm10Grade")) {
                            buffer.append("미세먼지 등급 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            grade = Integer.parseInt(xpp.getText());        // 등급 저장
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("pm25Grade")) {
                            buffer.append("초미세먼지 등급 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if(tag != null && tag.equals("item"))
                            buffer.append("\n");
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }
        //buffer.append("파싱 끝\n");
        switch (grade) {
            case 1:
                if (callback != null) {
                    callback.onImageSelected(0);
                }
                break;
            case 2:
                if (callback != null) {
                    callback.onImageSelected(1);
                }
                break;
            case 3:
                if (callback != null) {
                    callback.onImageSelected(2);
                }
                break;
            case 4:
                if (callback != null) {
                    callback.onImageSelected(3);
                }
                break;
        }
        return buffer.toString();
    }
}