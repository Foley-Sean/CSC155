#version 430

uniform float xinc, yinc, changeColor, oxinc, oyinc, scale, orbit, upDown, sxinc, syinc;
out vec4 gradientColor;

void main(void)
{
	if (gl_VertexID == 0){
		gl_Position = vec4(0.25,-0.25, 0.0, 1.0);
		if(changeColor == 1){
			gradientColor  = vec4(0.0, 1.0, 0.0, 1.0);
		}
		else if(changeColor == 0){
			gradientColor  = vec4(0.0, 1.0, 0.0, 1.0);
		}
		//orbit
		if(orbit == 1){
			gl_Position = vec4(0.25+oxinc,-0.25+oyinc, 0.0, 1.0);
		}
		//go up and down
		else if(upDown == 1){
			gl_Position = vec4(0.25+xinc,-0.25+yinc, 0.0, 1.0);
		}
		//scale
		else if(scale == 1){
			gl_Position = vec4(0.25+sxinc,-0.25+syinc, 0.0, 1.0);
		}
		//default

	}
    else if (gl_VertexID == 1){
	  gl_Position = vec4(-0.25,-0.25, 0.0, 1.0);
	  if(changeColor == 1){
		  gradientColor = vec4(1.0, 0.0, 0.0, 1.0);
	  }
	  else if(changeColor == 0){
		  gradientColor  = vec4(0.0, 1.0, 0.0, 1.0);
	  }
	  //orbit
	  if(orbit == 1){
		gl_Position = vec4(0.25+oxinc,-0.25+oyinc, 0.0, 1.0);
	  }
	  //go up and down
	  else if(upDown == 1){
		gl_Position = vec4(0.25+xinc,-0.25+yinc, 0.0, 1.0);
	  }
	  //scale
	  else if(scale == 1){
		gl_Position = vec4(0.25+sxinc,-0.25+syinc, 0.0, 1.0);
	  }
	  //default

    }

    else{
	  gl_Position = vec4(0.0, 0.4330127019, 0.0, 1.0);
	  if(changeColor == 1){
		  gradientColor = vec4(0.0, 0.0, 1.0, 1.0);
	  }
	  else if(changeColor == 0){
		  gradientColor  = vec4(0.0, 1.0, 0.0, 1.0);
	  }
	  if(changeColor == 1){
	 		  gradientColor = vec4(1.0, 0.0, 0.0, 1.0);
	 	  }
	 	  else if(changeColor == 0){
	 		  gradientColor  = vec4(0.0, 1.0, 0.0, 1.0);
	 	  }
	 	  //orbit
	 	  if(orbit == 1){
	 		gl_Position = vec4(0.25+oxinc,-0.25+oyinc, 0.0, 1.0);
	 	  }
	 	  //go up and down
	 	  else if(upDown == 1){
	 		gl_Position = vec4(0.25+xinc,-0.25+yinc, 0.0, 1.0);
	 	  }
	 	  //scale
	 	  else if(scale == 1){
	 		gl_Position = vec4(0.25+sxinc,-0.25+syinc, 0.0, 1.0);
	 	  }
	 	  //default

    }
}


/*
void main(void)
{ if (gl_VertexID == 0) gl_Position = vec4( 0.0, 0.0, 0.0, 1.0);
  else if (gl_VertexID == 1) gl_Position = vec4(0.0, 0.0, 0.0, 1.0);
  else gl_Position = vec4( 0.0, 0.0, 0.0, 1.0);
}
*/
