//JASON HANNAN

package bufmgr;

import global.Page;
import global.PageId;

public class FrameDesc {
    //Class Fields
    boolean dirtyBit;
    boolean validBit;
    boolean referenceBit;
    PageId diskPageNumber;
    int pinCount;
    Page page;

    public FrameDesc(){
        dirtyBit = false;
        validBit = false;
        referenceBit = false;
        diskPageNumber = -1;
        pinCount = 0;
        page = null;
    }

    public FrameDesc(PageId dPN, Page p){
        dirtyBit = false;
        validBit = true;
        referenceBit = false;
        diskPageNumber = dPN;
        pinCount = 1;
        page = p;
    }

    //Just Incase I Need Setters And Getters
    public boolean getDirtyBit(){ return dirtyBit; }
    public boolean getValidBit(){ return validBit; }
    public boolean getReferenceBit(){ return referenceBit; }
    public PageId getDiskPageNumber(){ return diskPageNumber; }
    public int getPinCount(){ return pinCount; }
    public Page getPage() { return page;}
    public void setDirtyBit(boolean dB){ dirtyBit= dB; }
    public void setValidBit(boolean vB){ validBit = vB; }
    public void setReferenceBit(boolean rB){ referenceBit = rB; }
    public void setDiskPageNumber(PageId dPN){ diskPageNumber = dPN; }
    public void reducePinCount(){
        pinCount = pinCount - 1;
        if(pinCount == 0){
            referenceBit = true;
        }
    }
    public void increasePinCount(){ pinCount = pinCount + 1; }
    public void setPage(Page p) { page = p; }

    //Check to see if pinCount is zero
    public boolean isPinZero(){
        if(pinCount == 0){
            return true;
        }
        else{
            return false;
        }
    }
    //Check to see if dirtyBit is set
    public boolean isDirtyTrue(){
        if(dirtyBit == true){
            return true;
        }
        else{
            return false;
        }
    }
    //Check to see if validBit is set
    public boolean isValidTrue(){
        if(validBit == true){
            return true;
        }
        else{
            return false;
        }
    }
    //Check to see if referenceBit is set
    public boolean isReferenceTrue(){
        if(referenceBit == true){
            return true;
        }
        else{
            return false;
        }
    }

    //Check to flush page
    public boolean checkFlush(){
        if(validBit == true && dirtyBit == true){
            return true;
        }
        else{
            return false;
        }
    }

    //Check to see if page is valid
    public boolean isPageValid(){
        if(validBit == false || (pinCount == 0 && referenceBit == false)){
            return false;
        }
        else{
            return true;
        }
    }
}