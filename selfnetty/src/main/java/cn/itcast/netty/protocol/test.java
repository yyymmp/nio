package cn.itcast.netty.protocol;

/**
 * @author jlz
 * @date 2022年01月17日 9:50
 */
class test {

    public static void main(String[] args) {
        String s = String.valueOf(" ");
        System.out.println(s);
        //m1();
    }

    public static String m1(){
        try {
            int i = 1 / 0;
            System.out.println("111");


        }catch (Exception e){
            throw new RuntimeException();
        }finally {
            System.out.println("catch块中抛出异常后执行");
        }

        return "111";
    }
}
