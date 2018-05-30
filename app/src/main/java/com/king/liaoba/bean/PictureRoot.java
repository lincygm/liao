package com.king.liaoba.bean;

/**
 * Created by gaomou on 2018/5/19.
 */

public class PictureRoot {

        private int status;
        private PictureBean data;
        public void setStatus(int status) {
            this.status = status;
        }
        public int getStatus() {
            return status;
        }

        public void setData(PictureBean data) {
            this.data = data;
        }
        public PictureBean getData() {
            return data;
        }

    }
