/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author jason
 */
public class JSPTag2CommentStream extends InputStream
{

    private final InputStream in;
    private boolean inTag=false;
    
    private Deque<Integer> queue = new ArrayDeque<>();

    public JSPTag2CommentStream(InputStream in)
    {
        this.in = in;
        
    }

    private byte[] buffer = new byte[1024];
    @Override
    public int read() throws IOException
    {
        refillBuffer();
        System.out.println("queue is"+queue);
        Integer i =queue.pollFirst();
        int nextChar = i!=null?i:-1;
        if(inTag){
            if (nextChar == '<')
            {
                if (queue.peekFirst() == '%')
                {
                    queue.pollFirst(); // drop it
                    queue.addFirst((int) '-');
                    queue.addFirst((int) '-');
                    queue.addFirst((int) '!');
//                queue.addFirst('<');
                }
                inTag=true;
            }
        }else{
            if (nextChar == '%')
            {
                if (queue.peekFirst() == '>')
                {
                    queue.pollFirst(); // drop it
                    queue.addFirst((int) '>');
                    queue.addFirst((int) '-');
                    nextChar='-';
//                queue.addFirst('<');
                }
                inTag = false;
            }
            
        }
        return nextChar;
    }

    private void refillBuffer() throws IOException
    {
        if(queue.size()==0){
            
            int bytesRead = in.read(buffer);
            for (int i = 0; i < bytesRead; i++)
            {
                queue.addLast(new Integer(buffer[i]));
            }
        }
    }

    @Override
    public int available() throws IOException
    {
        return super.available()+queue.size(); //To change body of generated methods, choose Tools | Templates.
    }
    
    

}
