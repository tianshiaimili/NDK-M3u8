#ifndef _HTTPPUT_H
#define _HTTPPUT_H

#define TSS_NUM_PER_TS 4 /* each ts will be splitted to 4 tss */
#define TSS_NUM_MERGE_TS 3 /* at least 3 tss needed to merge back ts */
#define TSS_NUM_MAX_FAIL 2 /* if 2 tss fail, then can NOT merge back */

// split to 3 strips + 1 parity
int splitToStrips(const char* src, const char* strip0, const char* strip1, const char* strip2, const char* strip3);
// split with MD5 checksum
int splitToStripsWithMD5(const char* src, const char* strip0, const char* strip1, const char* strip2, const char* strip3);
// splite to 3 strips only (no parity create)
int splitToStripsWithMD5Min(const char* src, const char* strip0, const char* strip1, const char* strip2);

// merge split to single file
int mergeStrips(const char* strip0, const char* strip1, const char* strip2, const char* file);
// merge split to single file with MD5 checking
int mergeStripsWithMD5(const char* strip0, const char* strip1, const char* strip2, const char* file);

// recover 1 split from other three
int genMissing(const char* stripA, const char* stripB, const char* stripC, const char* stripX);
// recover 1 split from other three (which with MD5 header), correctness of each strip would not check
int genMissingWithMD5(const char* stripA, const char* stripB, const char* stripC, const char* stripX);



#endif

