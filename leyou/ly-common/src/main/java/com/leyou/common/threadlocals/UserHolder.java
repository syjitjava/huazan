package com.leyou.common.threadlocals;

public class UserHolder {

    private static ThreadLocal<Long> Tl = new ThreadLocal<>();

    public static Long getTl() {
        return Tl.get();
    }

    public static void setTl(Long userId ) {
        Tl.set(userId);
    }
    public static void removeUser(){
        Tl.remove();
    }
}
