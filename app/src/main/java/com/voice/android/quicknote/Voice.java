package com.voice.android.quicknote;

import java.util.ArrayList;

/**
 * Created by lebron on 17-5-10.
 */

public class Voice {
    public ArrayList<Voice.WSBean> ws;

    public class WSBean {
        public ArrayList<Voice.CWBean> cw;
    }

    public class CWBean {
        public String w;
    }
}
