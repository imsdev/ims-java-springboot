//IMSCLOUT JOB (POK,999),'GAEBLER',                                    
// CLASS=A,MSGLEVEL=(1,1),REGION=32M,NOTIFY=GAEBLER,                   
// USER=GAEBLER,MSGCLASS=K                                             
//*                                                                    
//  JCLLIB ORDER=IMSCFG.IMSB.PROCLIB                                   
//ICALBMP  EXEC PROC=IMSBATCH,MBR=DFSDDLT0,PSB=DFSIVP2,IMSID=IMB1,     
//         SOUT='*',NBA=6,OBA=5,TIME=60                                
//PRINTDD  DD SYSOUT=*                                                 
//SYSUDUMP DD DUMMY                                                    
//SYSIN DD *                                                           
S1111 1 1 1   1IOPCB         AIB                                       
L   0010 STAK                                                          
L        ICAL  SENDRECV IMB1TOC  000500 00050 00050                    
L        DATA  HELLO FROM IMS T                                        
L        END                                                           
E      OK                                                              
//*                                                                    