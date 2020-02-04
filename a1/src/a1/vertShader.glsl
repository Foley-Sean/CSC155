#version 430

uniform float inc,yinc;

//original shader code

void main(void)
{
	if (gl_VertexID == 0){
		gl_Position = vec4( 0.25+inc,-0.25+yinc, 0.0, 1.0);
	}
    else if (gl_VertexID == 1){
	  gl_Position = vec4(-0.25+inc,-0.25+yinc, 0.0, 1.0);
    }
    else{
	  gl_Position = vec4(0.0+inc, 0.4330127019+yinc, 0.0, 1.0);
    }
}


/*
void main(void)
{ if (gl_VertexID == 0) gl_Position = vec4( 0.0, 0.0, 0.0, 1.0);
  else if (gl_VertexID == 1) gl_Position = vec4(0.0, 0.0, 0.0, 1.0);
  else gl_Position = vec4( 0.0, 0.0, 0.0, 1.0);
}
*/
