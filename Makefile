

all: $(EXEC)
		javac *.java
		java Client 139.165.99.199 ddi.uliege.be
		java Client 127.0.0.53 selector1._domainkey.ethias.be A
		java Client 13.80.155.61 nrb.be TXT
		java Client 139.165.214.214 ulg.ac.be TXT




clean:
		rm -f *.class $(EXEC) *~
