package xens.test;

import java.util.Scanner;

public class SM4Util {
    /* 密钥拓展 */
    private static int[] key_expand(int[] key_inputs){
        int[] key_rounds = new int[32];//32轮密钥
        int[] key = new int[4];//key_i
        int box_input,box_output;//盒变换输入输出
        //4个常数
        int[] fk = {0xa3b1bac6,0x56aa3350,0x677d9197,0xb27022dc};
        //32个固定参数
        int[] ck = {
                0x00070e15,0x1c232a31,0x383f464d,0x545b6269,
                0x70777e85,0x8c939aa1,0xa8afb6bd,0xc4cbd2d9,
                0xe0e7eef5,0xfc030a11,0x181f262d,0x343b4249,
                0x50575e65,0x6c737a81,0x888f969d,0xa4abb2b9,
                0xc0c7ced5,0xdce3eaf1,0xf8ff060d,0x141b2229,
                0x30373e45,0x4c535a61,0x686f767d,0x848b9299,
                0xa0a7aeb5,0xbcc3cad1,0xd8dfe6ed,0xf4fb0209,
                0x10171e25,0x2c333a41,0x484f565d,0x646b7279
        };

        for(int i =0;i<4;i++) key[i]=key_inputs[i]^fk[i];

        for(int i=0;i<32;i++){
            box_input = key[1]^key[2]^key[3]^ck[i];
            box_output = s_he(box_input);
            key_rounds[i] = key[0]^box_output^Shift(box_output,13)^Shift(box_output,23);
            key[0]=key[1];
            key[1]=key[2];
            key[2]=key[3];
            key[3]=key_rounds[i];
            //System.out.printf("turn:%d\tkey_rounds = %x\n",i,key_rounds[i]);
        }
        return key_rounds;
    }

    /* 加解密 */
    private static int[] decrypt(int[] text,int[] key_rounds,int mod){
        int box_input,box_output;//盒变换输入和输出

        for(int i=0;i<32;i++){
            int index = (mod==0)?i:(31-i);//通过改变key_rounds的顺序改变模式
            box_input = text[1]^text[2]^text[3]^key_rounds[index];
            box_output = s_he(box_input);
            int temp = text[0]^box_output^Shift(box_output,2)^Shift(box_output,10)^Shift(box_output,18)^Shift(box_output,24);
            text[0]=text[1];
            text[1]=text[2];
            text[2]=text[3];
            text[3]=temp;
        }
        return new int[]{
                text[3],text[2],text[1],text[0]
        };
    }

    /* 盒变换 */
    public static int s_he(int box_input){
        int box_output;//盒变换输出
        int[] input =new int[8];//单个盒变换输入
        int[] output =new int [4];//单个盒变换输出
        //s盒的参数
        int[][] s ={
                {0xd6,0x90,0xe9,0xfe,0xcc,0xe1,0x3d,0xb7,0x16,0xb6,0x14,0xc2,0x28,0xfb,0x2c,0x05},
                {0x2b,0x67,0x9a,0x76,0x2a,0xbe,0x04,0xc3,0xaa,0x44,0x13,0x26,0x49,0x86,0x06,0x99},
                {0x9c,0x42,0x50,0xf4,0x91,0xef,0x98,0x7a,0x33,0x54,0x0b,0x43,0xed,0xcf,0xac,0x62},
                {0xe4,0xb3,0x1c,0xa9,0xc9,0x08,0xe8,0x95,0x80,0xdf,0x94,0xfa,0x75,0x8f,0x3f,0xa6},
                {0x47,0x07,0xa7,0xfc,0xf3,0x73,0x17,0xba,0x83,0x59,0x3c,0x19,0xe6,0x85,0x4f,0xa8},
                {0x68,0x6b,0x81,0xb2,0x71,0x64,0xda,0x8b,0xf8,0xeb,0x0f,0x4b,0x70,0x56,0x9d,0x35},
                {0x1e,0x24,0x0e,0x5e,0x63,0x58,0xd1,0xa2,0x25,0x22,0x7c,0x3b,0x01,0x21,0x78,0x87},
                {0xd4,0x00,0x46,0x57,0x9f,0xd3,0x27,0x52,0x4c,0x36,0x02,0xe7,0xa0,0xc4,0xc8,0x9e},
                {0xea,0xbf,0x8a,0xd2,0x40,0xc7,0x38,0xb5,0xa3,0xf7,0xf2,0xce,0xf9,0x61,0x15,0xa1},
                {0xe0,0xae,0x5d,0xa4,0x9b,0x34,0x1a,0x55,0xad,0x93,0x32,0x30,0xf5,0x8c,0xb1,0xe3},
                {0x1d,0xf6,0xe2,0x2e,0x82,0x66,0xca,0x60,0xc0,0x29,0x23,0xab,0x0d,0x53,0x4e,0x6f},
                {0xd5,0xdb,0x37,0x45,0xde,0xfd,0x8e,0x2f,0x03,0xff,0x6a,0x72,0x6d,0x6c,0x5b,0x51},
                {0x8d,0x1b,0xaf,0x92,0xbb,0xdd,0xbc,0x7f,0x11,0xd9,0x5c,0x41,0x1f,0x10,0x5a,0xd8},
                {0x0a,0xc1,0x31,0x88,0xa5,0xcd,0x7b,0xbd,0x2d,0x74,0xd0,0x12,0xb8,0xe5,0xb4,0xb0},
                {0x89,0x69,0x97,0x4a,0x0c,0x96,0x77,0x7e,0x65,0xb9,0xf1,0x09,0xc5,0x6e,0xc6,0x84},
                {0x18,0xf0,0x7d,0xec,0x3a,0xdc,0x4d,0x20,0x79,0xee,0x5f,0x3e,0xd7,0xcb,0x39,0x48}
        };

        //将32位分成8个4位的数
        input[0]=(box_input&0xf0000000)>>>28;
        input[1]=(box_input&0x0f000000)>>>24;
        input[2]=(box_input&0x00f00000)>>>20;
        input[3]=(box_input&0x000f0000)>>>16;
        input[4]=(box_input&0x0000f000)>>>12;
        input[5]=(box_input&0x00000f00)>>>8;
        input[6]=(box_input&0x000000f0)>>>4;
        input[7]=box_input&0x0000000f;

        //变换操作
        output[0]=s[input[0]][input[1]];
        output[1]=s[input[2]][input[3]];
        output[2]=s[input[4]][input[5]];
        output[3]=s[input[6]][input[7]];

        //将4个8位字节合并为一个字
        box_output=sub_4x8(output[0],output[1],output[2],output[3]);
        return box_output;
    }

    /* 将4个8位字节合并成一个32位字节 */
    public static int sub_4x8(int s_1,int s_2,int s_3,int s_4){
        return ((s_1<<24)&0xff000000) | ((s_2<<16)&0x00ff0000) | ((s_3<<8)&0x0000ff00) | (s_4&0x000000ff);
        //return ((s_1<<24) | (s_2<<16) | (s_3<<8) | s_4);
    }
    /* 将input左移n位 */
    public static int Shift(int input,int n){
        return (input>>>(32-n))|(input<<n);
    }
    /* 读入128位16进制字符 */
    public static int[] read_16(){
        //输入16个字符
        System.out.println("请输入128bit的16进制数");System.out.print(">>>> ");
        Scanner in = new Scanner(System.in);
        //00000000000000000000000000000000
        String str = in.next();//获得输入的字符串

        int[] key_inputs = new int[4];
        key_inputs[0] = (int)Long.parseLong(str.substring(0,8),16);
        key_inputs[1] = (int)Long.parseLong(str.substring(8,16),16);
        key_inputs[2] = (int)Long.parseLong(str.substring(16,24),16);
        key_inputs[3] = (int)Long.parseLong(str.substring(24,32),16);
        return key_inputs;
    }
    public static void main(String[] args){
        int[] key_inputs,text_inputs;
        int[] key_rounds,text_outputs;
        int mod = 0;

        System.out.println("现在读入初始密钥");
        key_inputs = read_16();
        System.out.println("密钥读入完成，初始密钥为");
        for (int key_input : key_inputs) System.out.printf("%08x ", key_input);
        System.out.println();


        System.out.println("现在读入明文/密文");
        text_inputs = read_16();
        System.out.println("明文/密文读入完成，明文/密文为");
        for(int text_input :text_inputs) System.out.printf("%08x ", text_input);
        System.out.println();

        System.out.print("现在读入加/解密模式，键入0为加密，键入1为解密\n>>>>");
        do{
            mod = new Scanner(System.in).nextInt();
            if(mod!=0 && mod!=1) System.out.print("键入错误!请键入数字0或数字1\n>>>>");
        }while(mod!=0 && mod!=1);

        key_rounds = key_expand(key_inputs);
        text_outputs = decrypt(text_inputs,key_rounds,mod);

        System.out.println( (mod==0)?"加密完成! 加密结果为":"解密完成! 解密结果为");
        for(int text:text_outputs) System.out.printf("%08x ",text);
    }
}