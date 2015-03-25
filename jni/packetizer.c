#include <android/log.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
/*
#include <android/log.h>
#define LOG_TAG "Packetizer"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define printf			LOGW
*/
#include "packetizer.h"
#define TAG    "LogUtils" // 这个是自定义的LOG的标识
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__) // 定义LOGD类型

// Constants are the integer part of the sines of integers (in radians) * 2^32.
const uint32_t k[64] = {
0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee ,
0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501 ,
0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be ,
0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821 ,
0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa ,
0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8 ,
0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed ,
0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a ,
0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c ,
0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70 ,
0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05 ,
0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665 ,
0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039 ,
0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1 ,
0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1 ,
0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391 };

// r specifies the per-round shift amounts
const uint32_t r[] = {7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
                      5,  9, 14, 20, 5,  9, 14, 20, 5,  9, 14, 20, 5,  9, 14, 20,
                      4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
                      6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21};

// leftrotate function definition
#define LEFTROTATE(x, c) (((x) << (c)) | ((x) >> (32 - (c))))


void to_bytes(uint32_t val, uint8_t *bytes)
{
    bytes[0] = (uint8_t) val;
    bytes[1] = (uint8_t) (val >> 8);
    bytes[2] = (uint8_t) (val >> 16);
    bytes[3] = (uint8_t) (val >> 24);
}

uint32_t to_int32(const uint8_t *bytes)
{
    return (uint32_t) bytes[0]
        | ((uint32_t) bytes[1] << 8)
        | ((uint32_t) bytes[2] << 16)
        | ((uint32_t) bytes[3] << 24);
}

void getMD5(const uint8_t *initial_msg, size_t initial_len, uint8_t *digest) {

    // These vars will contain the hash
    uint32_t h0, h1, h2, h3;

    // Message (to prepare)
    uint8_t *msg = NULL;

    size_t new_len, offset;
    uint32_t w[16];
    uint32_t a, b, c, d, i, f, g, temp;

    // Initialize variables - simple count in nibbles:
    h0 = 0x67452301;
    h1 = 0xefcdab89;
    h2 = 0x98badcfe;
    h3 = 0x10325476;

    //Pre-processing:
    //append "1" bit to message
    //append "0" bits until message length in bits = 448 (mod 512)
    //append length mod (2^64) to message

    for (new_len = initial_len + 1; new_len % (512/8) != 448/8; new_len++)
        ;

    msg = (uint8_t*)malloc(new_len + 8);
    memcpy(msg, initial_msg, initial_len);
    msg[initial_len] = 0x80; // append the "1" bit; most significant bit is "first"
    for (offset = initial_len + 1; offset < new_len; offset++)
        msg[offset] = 0; // append "0" bits

    // append the len in bits at the end of the buffer.
    to_bytes(initial_len*8, msg + new_len);
    // initial_len>>29 == initial_len*8>>32, but avoids overflow.
    to_bytes(initial_len>>29, msg + new_len + 4);

    // Process the message in successive 512-bit chunks:
    //for each 512-bit chunk of message:
    for(offset=0; offset<new_len; offset += (512/8)) {

        // break chunk into sixteen 32-bit words w[j], 0 = j = 15
        for (i = 0; i < 16; i++)
            w[i] = to_int32(msg + offset + i*4);

        // Initialize hash value for this chunk:
        a = h0;
        b = h1;
        c = h2;
        d = h3;

        // Main loop:
        for(i = 0; i<64; i++) {

            if (i < 16) {
                f = (b & c) | ((~b) & d);
                g = i;
            } else if (i < 32) {
                f = (d & b) | ((~d) & c);
                g = (5*i + 1) % 16;
            } else if (i < 48) {
                f = b ^ c ^ d;
                g = (3*i + 5) % 16;
            } else {
                f = c ^ (b | (~d));
                g = (7*i) % 16;
            }

            temp = d;
            d = c;
            c = b;
            b = b + LEFTROTATE((a + f + k[i] + w[g]), r[i]);
            a = temp;

        }

        // Add this chunk's hash to result so far:
        h0 += a;
        h1 += b;
        h2 += c;
        h3 += d;

    }

    // cleanup
    free(msg);

    //var char digest[16] := h0 append h1 append h2 append h3 //(Output is in little-endian)
    to_bytes(h0, digest);
    to_bytes(h1, digest + 4);
    to_bytes(h2, digest + 8);
    to_bytes(h3, digest + 12);
}


// split to 3 strips + 1 parity
int splitToStrips(const char* src, const char* strip0, const char* strip1, const char* strip2, const char* strip3)
{
    FILE *fp, *d0, *d1, *d2, *dd;
    char buffer[12];
    int* data;
    int parity;
    int bread;

    printf("split file\n");

    if ((fp=fopen(src, "r+b")) == NULL) {
	printf("Cannot open source file to split %s!\n", src);
	return -1;
    }

    if ((d0=fopen(strip0, "w+b")) == NULL) {
	printf("Cannot open source file to split0 %s!\n", strip0);
	return -1;
    }
    if ((d1=fopen(strip1, "w+b")) == NULL) {
	printf("Cannot open source file to split1 %s!\n", strip1);
	return -1;
    }
    if ((d2=fopen(strip2, "w+b")) == NULL) {
	printf("Cannot open source file to split2 %s!\n", strip2);
	return -1;
    }
    if ((dd=fopen(strip3, "w+b")) == NULL) {
	printf("Cannot open source file to split3 %s!\n", strip3);
	return -1;
    }

    while (1) {
    	bread = fread(buffer, 4, 3, fp);
    	if (bread < 3) {
    		data[2] = 0;
    	    if (bread < 2) data[1] = 0;
    	    if (bread < 1) break;
    	}

		data = (int*)buffer;
		parity = (data[0]^data[1])^data[2];

		fwrite((char*)&data[0], 4, 1, d0);
		fwrite((char*)&data[1], 4, 1, d1);
		fwrite((char*)&data[2], 4, 1, d2);
		fwrite((char*)&parity, 4, 1, dd);

		if (bread < 3) break;
    }

    fclose(fp);
    fclose(d0);
    fclose(d1);
    fclose(d2);
    fclose(dd);

    return 0;
}	

/* align_size has to be a power of two !! */
void *aligned_malloc(size_t size, size_t align_size) {

  char *ptr,*ptr2,*aligned_ptr;
  int align_mask = align_size - 1;

  ptr=(char *)malloc(size + align_size + sizeof(int));
  if(ptr==NULL) return(NULL);

  ptr2 = ptr + sizeof(int);
  aligned_ptr = ptr2 + (align_size - ((size_t)ptr2 & align_mask));


  ptr2 = aligned_ptr - sizeof(int);
  *((int *)ptr2)=(int)(aligned_ptr - ptr);

  return(aligned_ptr);
}


void aligned_free(void *ptr) {

  int *ptr2=(int *)ptr - 1;
  ptr -= *ptr2;
  /**
   * 函数名: free
	功 能: 释放已分配的块
	用 法: void free(void *ptr);
   *
   */
  free(ptr);

}

int write2File(const char* data, const char* filename, int len)
{
	FILE *fp;

    if ((fp=fopen(filename, "w+b")) == NULL) {
    	printf("Cannot create file  %s!\n", filename);
    return -1;
    }

//    printf("Write to file [%d]: %s\n", len, filename);
    fwrite(data, 1, len, fp);
    fclose(fp);
    return 0;
}

// 4 byte aligned memory would be created
// please call aligned_free to free the memory after use
int read2Mem(char** data, const char* filename, int* len)
{
	FILE *fp; 

    if ((fp=fopen(filename, "r+b")) == NULL) {
    	printf("Cannot read file  %s!\n", filename);
    return -1;
    }

    fseek(fp, 0L, SEEK_END);
    int sz = ftell(fp);
    fseek(fp, 0L, SEEK_SET);

    *data = aligned_malloc(sz, 4);
    char *p = *data;

    while(1) {
    	int bread = fread(p, 1, 1024, fp);
    	if (bread <=0) break;
    	p += bread;
    }

    fclose(fp);
    *len = sz;
    return 0;
}


// split to 3 strips + 1 parity
// preserve first 4bits (TS size) + 16bits (MD5 checksum)
int splitToStripsWithMD5(const char* src, const char* strip0, const char* strip1, const char* strip2, const char* strip3)
{
    FILE *fp; 
    char *p0, *p1, *p2, *p3;
    char *p[4];
    char *fn[4];
    char buffer[12];
    int* data;
    int parity;
    int bread;
    int i;
    int *pt;

    printf("split file\n");

    if ((fp=fopen(src, "r+b")) == NULL) {
	printf("Cannot open source file to split %s!\n", src);
	return -1;
    }
    // get data size and allocate memory for further process
    fseek(fp, 0L, SEEK_END);
    int sz = ftell(fp);
    fseek(fp, 0L, SEEK_SET);
    int ssz = ((sz+11) / 12)*4 + 20;

    for (i=0; i<4; i++) {
    	p[i] = aligned_malloc(ssz, 4);		// align to integer
    	if (p[i] == NULL) {
    		printf("can't allocate aligned mem! (%d)", ssz);
    	}

    	pt = (int*)p[i];
    	pt[0] = sz;
    }

    p0= p[0]+20;
    p1= p[1]+20;
    p2= p[2]+20;
    p3= p[3]+20;

    while (1) {
    	bread = fread(buffer, 1, 12, fp);


    	if (bread < 12) {
    		int padding = 12 - bread;
    		for (i=0; i<padding; i++) {
    			buffer[bread+i] = 0;
    		}
    	}

		data = (int*)buffer;
		parity = (data[0]^data[1])^data[2];

		memcpy(p0, (char*)&data[0], 4);
		memcpy(p1, (char*)&data[1], 4);
		memcpy(p2, (char*)&data[2], 4);
		memcpy(p3, (char*)&parity, 4);

		p0+=4; p1+=4; p2+=4; p3+=4;

		if (bread < 12) {
//			printf("end of data :%d %x %x %x\n", bread, data[0], data[1], data[2]);
			break;
		}	
    }

    int res;
    fn[0] = (char*)strip0;
    fn[1] = (char*)strip1;
    fn[2] = (char*)strip2;
    fn[3] = (char*)strip3;
    for (i=0; i<4; i++) {
    	getMD5((uint8_t*)(p[i]+20), (size_t)(ssz-20), (uint8_t*)(p[i]+4));
    	res = write2File(p[i], fn[i], ssz);
    	aligned_free(p[i]);
    	if (res < 0) return -1;
    }

    fclose(fp);

    return 0;
}


// split to 3 strips (no parity)
// preserve first 4bits (TS size) + 16bits (MD5 checksum)
int splitToStripsWithMD5Min(const char* src, const char* strip0, const char* strip1, const char* strip2)
{
    FILE *fp;
    char *p0, *p1, *p2;
    char *p[3];
    char *fn[3];
    char buffer[12];
    int* data;
    int parity;
    int bread;
    int i;
    int *pt;
    
    printf("split file\n");
//    fflush();
    LOGI("----------the src file to split %s!\n", src);
    LOGI("----------the strip1 file to split %s!\n", strip1);
    LOGI("------------------------------ %s\n", strip2);
    LOGI("----------Canopen source file to split %s!\n", src);
//    __android_log_print(ANDROID_LOG_INFO, "----------Canopen source file to split %s!\n", src);
    if ((fp=fopen(src, "r+b")) == NULL) {
//    	LOGI("Cannot open source file to split %s!\n", src);
        printf("Cannot open source file to split %s!\n", src);
        LOGI("----------Canopen source file to split %s\n", src);
        return -1;
    }
    // get data size and allocate memory for further process
    /***
     * fseek 用于二进制方式打开的文件,移动文件读写指针位置
     * SEEK_END： 文件结尾
　　	其中SEEK_SET,SEEK_CUR和SEEK_END和依次为0，1和2.
　　	简言之：
　　	fseek(fp,100L,0);把fp指针移动到离文件开头100字节处；
　	　fseek(fp,100L,1);把fp指针移动到离文件当前位置100字节处；
   　	fseek(fp,100L,2);把fp指针退回到离文件结尾100字节处。
     */
    fseek(fp, 0L, SEEK_END);

    //函数 ftell 用于得到文件位置指针当前位置相对于文件首的偏移字节数。在随机方式存取文件时，由于文件位置频繁的前后移动，程序不容易确定文件的当前位置
    //使用fseek函数后再调用函数ftell()就能非常容易地确定文件的当前位置
    int sz = ftell(fp);
    fseek(fp, 0L, SEEK_SET);

    int ssz = ((sz+11) / 12)*4 + 20;
    
    for (i=0; i<3; i++) {
        p[i] = aligned_malloc(ssz, 4);		// align to integer
        if (p[i] == NULL) {
            printf("can't allocate aligned mem! (%d)", ssz);
//            fflush();
        }
        
        pt = (int*)p[i];
        pt[0] = sz;
    }
    
    p0= p[0]+20;
    p1= p[1]+20;
    p2= p[2]+20;
    //p3= p[3]+20;
    
    while (1) {
    	//fread是一个函数 从一个文件流中读数据，最多读取count个元素，每个元素size字节，如果调用成功返回实际读取到的元素个数，如果不成功或读到文件末尾返回 0
        bread = fread(buffer, 1, 12, fp);
        
        
        if (bread < 12) {
            int padding = 12 - bread;
            for (i=0; i<padding; i++) {
                buffer[bread+i] = 0;
            }
        }
        
        data = (int*)buffer;
    //    parity = (data[0]^data[1])^data[2];
        //函数名: memcpy
        //功 能: 从源source中拷贝n个字节到目标destin中 void *memcpy(void *destin, void *source, unsigned n);


        memcpy(p0, (char*)&data[0], 4);
        memcpy(p1, (char*)&data[1], 4);
        memcpy(p2, (char*)&data[2], 4);
      //  memcpy(p3, (char*)&parity, 4);
        
        p0+=4; p1+=4; p2+=4; //p3+=4;
        
        if (bread < 12) {
            //			printf("end of data :%d %x %x %x\n", bread, data[0], data[1], data[2]);
            break;
        }
    }
    
    int res;
    fn[0] = (char*)strip0;
    fn[1] = (char*)strip1;
    fn[2] = (char*)strip2;
    //fn[3] = (char*)strip3;
    for (i=0; i<3; i++) {
        getMD5((uint8_t*)(p[i]+20), (size_t)(ssz-20), (uint8_t*)(p[i]+4));
        res = write2File(p[i], fn[i], ssz);
        aligned_free(p[i]);
        if (res < 0) return -1;
    }
    /**
     * 函数名: fclose
	功 能: 关闭一个流。成功返回0；失败是返回EOF。
	用 法: int fclose(FILE *stream);
     *
     */
    fclose(fp);
    
    return 0;
}


// display MD5 checksum
void printMD5(const uint8_t *bytes)
{
	uint8_t *p;
	p = (uint8_t *)bytes;
	uint32_t h0 = to_int32(p);
	uint32_t h1 = to_int32(p+4);
	uint32_t h2 = to_int32(p+8);
	uint32_t h3 = to_int32(p+12);

	printf("MD5:%x %x %x %x", h0, h1, h2, h3);
}


// merge split to single file
int mergeStripsWithMD5(const char* strip0, const char* strip1, const char* strip2, const char* file)
{
    FILE *fp;
    char currBuf[12];
    char* p[3];
    int len[3];
    int tsLen;
    int *tmp;
    char md5[16];
    char* fn[3];
    char *p0, *p1, *p2;
    int i, j;

    printf("Merge files\n");


    LOGI("----------the file file to split %s!\n", file);
    LOGI("----------the strip0 file to split %s!\n", strip0);
    LOGI("------------------------------ %s\n", strip2);
    LOGI("----------the source file to split %s!\n", file);
    if ((fp=fopen(file, "w+b")) == NULL) {
	printf("Cannot open source file to merge %s!\n", file);
	return -1;
    }

    // preliminary : (1)check file integrity with MD5, (2) get TS size
    fn[0] = (char*)strip0;
    fn[1] = (char*)strip1;
    fn[2] = (char*)strip2;
    for (i=0; i<3; i++) {
        read2Mem(&p[i], fn[i], &len[i]);
		getMD5((uint8_t*)(p[i]+20), (size_t)(len[i]-20), (uint8_t*)md5);
		int diff = strncmp(p[i]+4, md5, 16);
//		printMD5((const uint8_t*)md5);
//		printf("\n");
		if (diff != 0) {
			printf("[Error: checksum not match");
			for (j=0; j<=i; j++)
				aligned_free(p[j]);
			return -1;
		}
    }
    tmp = (int*)p[0];
    tsLen = *tmp;
//    printf("TS len:%d\n", tsLen);

    int curlen = 0;
    p0 = p[0]+20;
    p1 = p[1]+20;
    p2 = p[2]+20;

    while (1) {
    	memcpy(currBuf, p0, 4);
    	memcpy(currBuf+4, p1, 4);
    	memcpy(currBuf+8, p2, 4);
    	if (tsLen-curlen >=12) {
    		fwrite(currBuf, 12, 1, fp);
    	} else {
    		fwrite(currBuf, tsLen-curlen, 1, fp);
    		break;
    	}
    	curlen+=12;
    	p0+=4; p1+=4; p2+=4;
    }

    fclose(fp);
    for (i=0; i<3; i++)
    	aligned_free(p[i]);

    return 0;
}



// merge split to single file
int mergeStrips(const char* strip0, const char* strip1, const char* strip2, const char* file)
{
    FILE *fp, *d0, *d1, *d2;
    char buffer[12];
    char buffer1[12];
    char *currBuf, *lastBuf, *tmpPtr;
    int bread;
    int* data;

    printf("Merge files\n");

    if ((fp=fopen(file, "w+b")) == NULL) {
	printf("Cannot open source file to merge %s!\n", file);
	return -1;
    }

    if ((d0=fopen(strip0, "r+b")) == NULL) {
	printf("Cannot open source file strip0 %s!\n", strip0);
	return -1;
    }
    if ((d1=fopen(strip1, "r+b")) == NULL) {
	printf("Cannot open source file strip1 %s!\n", strip1);
	return -1;
    }
    if ((d2=fopen(strip2, "r+b")) == NULL) {
	printf("Cannot open source file strip2 %s!\n", strip2);
	return -1;
    }

    currBuf = buffer;
    lastBuf = buffer1;
    memset(currBuf, 0, 12);
    memset(lastBuf, 0, 12);

    bread = fread(lastBuf, 4, 1, d0);
    if (!bread) return -1;
    bread = fread(lastBuf+4, 4, 1, d1);
    if (!bread) return -1;
    bread = fread(lastBuf+8, 4, 1, d2);
    if (!bread) return -1;

    while (1) {
    	bread = fread(currBuf, 4, 1, d0);
	if (!bread) break;
    	bread = fread(currBuf+4, 4, 1, d1);
	if (!bread) break;
    	bread = fread(currBuf+8, 4, 1, d2);
	if (!bread) break;

        fwrite(lastBuf, 12, 1, fp);
	tmpPtr = lastBuf;
	lastBuf = currBuf;
	currBuf = tmpPtr;
    }
  
    data = (int*)lastBuf;
    if ((data[1] == 0) && (data[2] == 0)) fwrite(lastBuf, 4, 1, fp);
       else if (data[2] == 0) fwrite(lastBuf, 8, 1, fp);
           else fwrite(lastBuf, 12, 1, fp);

    fclose(fp);
    fclose(d0);
    fclose(d1);
    fclose(d2);

    return 0;
}	


int genMissingWithMD5(const char* stripA, const char* stripB, const char* stripC, const char* stripX)
{
    char* p[4];
    int len[4];
    int tsslen;
    int *pt;
    int *p0, *p1, *p2;
    int ss;
    int i;

    printf("Generate file\n");

    read2Mem(&p[0], stripA, &len[0]);
    read2Mem(&p[1], stripB, &len[1]);
    read2Mem(&p[2], stripC, &len[2]);

    if ((len[0] != len[1]) || (len[0] != len[2])) {
    	printf("Invalid segments to gen the missing part\n");
    	return -1;
    }
    tsslen = len[0];

    // allocate mem for generated part
    p[3] = aligned_malloc(tsslen, 4);
    memcpy(p[3], p[0], 4);	// copy ts len to the new part

    p0 = (int*)(p[0]+20);
    p1 = (int*)(p[1]+20);
    p2 = (int*)(p[2]+20);
    pt = (int*)(p[3]+20);
    //curlen = 0;

    ss = (tsslen-20)/4;

    for (i=0; i<ss; i++) {
    	*pt = ((*p0)^(*p1))^(*p2);
    	p0++; p1++; p2++; pt++;
    }
    // cal and place MD5 into header
	getMD5((uint8_t*)(p[3]+20), (size_t)(tsslen-20), (uint8_t*)(p[3]+4));
	int res = write2File(p[3], stripX, tsslen);
	aligned_free(p[3]);
	if (res < 0) return -1;

    return 0;
}		

// It only gen the missing strip, wouldn't check MD5 of provided strips
int genMissing(const char* stripA, const char* stripB, const char* stripC, const char* stripX)
{
    FILE *fp, *d0, *d1, *d2;
    int bread;
    int data0, data1, data2, rdata;

    printf("Generate file\n");

    if ((fp=fopen(stripX, "w+b")) == NULL) {
	printf("Cannot open source file stripX %s!\n", stripX);
	return -1;
    }

    if ((d0=fopen(stripA, "r+b")) == NULL) {
	printf("Cannot open source file stripA %s!\n", stripA);
	return -1;
    }
    if ((d1=fopen(stripB, "r+b")) == NULL) {
	printf("Cannot open source file stripB %s!\n", stripB);
	return -1;
    }
    if ((d2=fopen(stripC, "r+b")) == NULL) {
	printf("Cannot open source file stripC %s!\n", stripC);
	return -1;
    }

    while (1) {
		bread = fread((char*)&data0, 4, 1, d0);
		if (!bread) break;
			bread = fread((char*)&data1, 4, 1, d1);
		if (!bread) break;
			bread = fread((char*)&data2, 4, 1, d2);
		if (!bread) break;

		rdata = (data0^data1)^data2;
		fwrite((char*)&rdata, 4, 1, fp);
    }

    fclose(fp);
    fclose(d0);
    fclose(d1);
    fclose(d2);

    return 0;
}


/**
 * 转换jstring和char 保证路径正确
 */
char* jstringTostring(JNIEnv *env, jstring jstr)
{
       char* rtn = NULL;
       jclass clsstring = (*env)->FindClass(env,"java/lang/String");
       jstring strencode = (*env)->NewStringUTF(env,"utf-8");
       jmethodID mid = (*env)->GetMethodID(env,clsstring, "getBytes", "(Ljava/lang/String;)[B");
       jbyteArray barr= (jbyteArray)(*env)->CallObjectMethod(env,jstr, mid, strencode);
       jsize alen = (*env)->GetArrayLength(env,barr);
       jbyte* ba = (*env)->GetByteArrayElements(env,barr, JNI_FALSE);
       if (alen > 0)
       {
                 rtn = (char*)malloc(alen + 1);
                 memcpy(rtn, ba, alen);
                 rtn[alen] = 0;
       }
       (*env)->ReleaseByteArrayElements(env,barr, ba, 0);
       return rtn;
}
