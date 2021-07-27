package org.techtown.yeminapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoronaFragment extends Fragment {
    TextView dateTextView,text;
    //XmlPullParser xpp;
    String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_corona, container, false);

        dateTextView=rootView.findViewById(R.id.textView3);
        long nowDate = System.currentTimeMillis();
        SimpleDateFormat sDate = new SimpleDateFormat("yyyyMMdd");
        String getTime = sDate.format(nowDate);
        dateTextView.append(getTime);

        text = rootView.findViewById(R.id.textView7);

        Button button = rootView.findViewById(R.id.button);
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
            case R.id.button:
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
        StringBuffer buffer = new StringBuffer();
        String key = "l3gdzJOEM456Gd7ECjzivcwQfPEsnXYi5mgNbRzsFRVYLolt2XaDTuKX4SAut3z%2BIS6clH73BBDphPl3hPzR7g%3D%3D";

        String date = dateTextView.getText().toString();

        String queryUrl = "http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?serviceKey="
                + key +"&pageNo=1&numOfRows=10&startCreateDt="+date;

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
                        else if(tag != null && tag.equals("accDefRate")) {
                            buffer.append("누적 확진률 : ");
                            xpp.next();
                            buffer.append(xpp.getText());   //title 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("accExamCnt")) {
                            buffer.append("누적 검사 수 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("accExamCompCnt")) {
                            buffer.append("누적 검사 완료수 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("careCnt")) {
                            buffer.append("치료중 환자 수 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("clearCnt")) {
                            buffer.append("격리해제 수 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("createDt")) {
                            buffer.append("등록일시분초 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("deathCnt")) {
                            buffer.append("사망자 수 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("decideCnt")) {
                            buffer.append("확진자 수 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("examCnt")) {
                            buffer.append("검사진행 수 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("resutlNegCnt")) {
                            buffer.append("결과 음성 수 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("seq")) {
                            buffer.append("게시글번호(감염현황 고유값) : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("stateDt")) {
                            buffer.append("기준일 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("stateTime")) {
                            buffer.append("기준시간 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag != null && tag.equals("updateDt")) {
                            buffer.append("수정일시분초 : ");
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
        buffer.append("파싱 끝\n");
        return buffer.toString();
    }

}