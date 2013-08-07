default files = FLUX_DIR;

in+ "" |
open-file | 
generic-xml("metadata")|
morph(files + "opus-xmetadissplus-to-lobid.xml")|
encode-ntriples-with-subject-as-parameter(subject=""+subject)|
write(out+"");

