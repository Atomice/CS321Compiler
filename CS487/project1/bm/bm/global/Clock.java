//JASON HANNAN

package bufmgr;

import java.util.ArrayList;

public class Clock {
    public int currentIndex;

    public Clock(){ currentIndex = 0; }

    public int pickVictim(ArrayList<FrameDesc> bufferPool){
        int startIndex = currentIndex;
        FrameDesc cFrame = null;
        int bufferSize = bufferPool.size();

        for(int i = 0; i < 2; ++i){
            cFrame = bufferPool.get(currentIndex);
            if(!(cFrame.isPageValid())){
                return currentIndex;
            }
            if(cFrame.isPinZero()){
                if(cFrame.isReferenceTrue()){
                    cFrame.setReferenceBit(false);
                }
                else{
                    return currentIndex;
                }
            }
            currentIndex = (currentIndex + 1) % bufferSize;
            while(currentIndex != startIndex){
                cFrame = bufferPool.get(currentIndex);
                if(!(cFrame.isPageValid())){
                    return currentIndex;
                }
                if(cFrame.isPinZero()){
                    if(cFrame.isReferenceTrue()){
                        cFrame.setReferenceBit(false);
                    }
                    else{
                        return currentIndex;
                    }
                }
                currentIndex = (currentIndex + 1) % bufferSize;
            }
        }

        throw new IllegalStateException("No Victim");
    }
}