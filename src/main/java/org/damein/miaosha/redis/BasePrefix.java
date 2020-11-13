package org.damein.miaosha.redis;

public abstract class BasePrefix implements keyPrefix{
    private int expireSeconds;
    private String prefix;


    public BasePrefix(String prefix){//0代表永不过期，这里用public是因为抽象类本身就不可实例化，不用private限定了
        this(0,prefix);//this( ) 用来访问本类的构造方法（构造方法是类的一种特殊方法，
        // 方法名称和类名相同，没有返回值,给下面的构造函数赋值了
    }

    public BasePrefix(int expireSeconds,String prefix){
        this.expireSeconds=expireSeconds;
        this.prefix=prefix;
    }

    public int expireSeconds(){//0代表永不过期
        return expireSeconds;
    }

    public String getPrefix(){
        String className=getClass().getSimpleName();
        return className+":"+prefix;
    }
}
