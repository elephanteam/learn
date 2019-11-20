package com.limit.learn.entity;

import java.util.List;

public class LoginBean {

    /**
     * errcode : 0
     * msg : Success
     * data : {"token":"VnvzTxNg4gdibL/5Sqvk0tduIL13y5iOCMKl5PepjOY=","username":"vivo","uid":"206","localid":"f2259ccd-658a-4f74-8678-a05117b3db00","eth_address":["0xee4a6f568f20b373b62cf4a579d65a90759e3155","0x9d70658817ac061fb080aa15cbbc022c33a82d53","0x70aefe8d97ef5984b91b5169418f3db283f65a29","0x81f2bd23ee8e4af042acd4d14028a10ff7d5df49"],"mid":"wkp111"}
     */

    private int errcode;
    private String msg;

    private DataBean data;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * token : VnvzTxNg4gdibL/5Sqvk0tduIL13y5iOCMKl5PepjOY=
         * username : vivo
         * uid : 206
         * localid : f2259ccd-658a-4f74-8678-a05117b3db00
         * eth_address : ["0xee4a6f568f20b373b62cf4a579d65a90759e3155","0x9d70658817ac061fb080aa15cbbc022c33a82d53","0x70aefe8d97ef5984b91b5169418f3db283f65a29","0x81f2bd23ee8e4af042acd4d14028a10ff7d5df49"]
         * mid : wkp111
         */

        private String token;
        private String username;
        private String uid;
        private String localid;
        private String mid;
        private List<String> eth_address;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getLocalid() {
            return localid;
        }

        public void setLocalid(String localid) {
            this.localid = localid;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public List<String> getEth_address() {
            return eth_address;
        }

        public void setEth_address(List<String> eth_address) {
            this.eth_address = eth_address;
        }
    }
}
