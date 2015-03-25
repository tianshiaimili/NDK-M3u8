#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#include "packetizer.h"


int main(int argc, char* argv[])
{
    printf("Test program for packetizer\n");

    splitToStripsWithMD5Min("seg01.ts", "seg01-0.tss", "seg01-1.tss", "seg01-2.tss");
//    splitToStripsWithMD5("seg01.ts", "seg01-0.tss", "seg01-1.tss", "seg01-2.tss", "seg01-3.tss");

    //genMissingWithMD5("seg01-1.tss", "seg01-3.tss", "seg01-0.tss", "seg01-2r.tss");

    mergeStripsWithMD5("seg01-0.tss", "seg01-1.tss", "seg01-2.tss", "seg01r.ts");
    
    
    return 0;	
}	


