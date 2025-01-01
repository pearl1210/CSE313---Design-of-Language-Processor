// Online C compiler to run C program online
// also do for a+bb
#include <stdio.h>
#include <string.h>

int main() {
    
    char c[100];
    int clen,valid=1;
    printf("Enter a string: ");
    scanf("%s",c);
    
    clen=strlen(c);
    if (clen<2){
        valid=0;
    }
    if (valid && (c[clen-1]!='b' && c[clen-2]!='b')){
        valid=0;
    }
    if(valid){
        for(int i=0;i<clen-2;i++){
            if (c[i]!='a'){
                valid = 0;
                break;
                
            }
        }
    }
    if (valid) {
        printf("Valid string!! \n");
    } else {
        printf("Invalid string!! Try Again!!\n");
    }
    

    return 0;
}
