package org.techtown.yeminapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CoronaFragment extends Fragment {
    TextView dateTextView,textGu;
    String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_corona, container, false);

        dateTextView=rootView.findViewById(R.id.textView3);
        //long nowDate = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sDate = new SimpleDateFormat("yyyyMMdd");
        String getTime = sDate.format(cal.getTime());//(nowDate);
        dateTextView.append(getTime);

        textGu = rootView.findViewById(R.id.textView6);

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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        data = getGuXmlData();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {textGu.setText(data);
                            }
                        });
                    }
                }).start();
                break;
        }
    }   //mOnClick method.

    //api와 연결
    String getGuXmlData() {
        StringBuffer buffer = new StringBuffer();
        String key = "l3gdzJOEM456Gd7ECjzivcwQfPEsnXYi5mgNbRzsFRVYLolt2XaDTuKX4SAut3z%2BIS6clH73BBDphPl3hPzR7g%3D%3D";
        String date = dateTextView.getText().toString();
        String queryUrl = "http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson?serviceKey="
                + key +"&pageNo=1&numOfRows=10&startCreateDt="+date;

        try {
            URL url = new URL(queryUrl);            //문자열로 된 요청 url을 URL 객체로 생성
            InputStream is = url.openStream();      //url 위치로 입력스트림 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));       //inputstream으로부터 xml 입력받기

            String tag;
            int count=1;

            xpp.next();
            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        //buffer.append("파싱 시작\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();    //태그 이름 얻어오기
                        if(tag != null && tag.equals("item"));
                        else if(tag != null && tag.equals("gubun")) {//시도명
                            if(count==19) {
                                xpp.next();
                                buffer.append("대한민국, ");
                            }
                            else {
                                xpp.next();
                                buffer.append(xpp.getText() + ", ");
                            }
                        }
                        else if(tag != null && tag.equals("defCnt")) {
                            //buffer.append("확진자 수 : ");
                            xpp.next();
                            buffer.append(xpp.getText()+", ");
                        }
                        else if(tag != null && tag.equals("incDec")) {//일일 증가량
                            buffer.append("(+ ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append(" )");
                            count++;
                        }
                        else if(tag != null && tag.equals("deathCnt")) {
                            //buffer.append("사망자 수: ");
                            xpp.next();
                            buffer.append(xpp.getText()+", ");
                        }
                        else if(tag != null && tag.equals("totalCount")) {
                            //buffer.append("사망자 수: ");
                            xpp.next();
                            if(xpp.getText().equals("0"))
                                buffer.append("아직 업데이트되지 않았습니다.");
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
        return buffer.toString();
    }

}