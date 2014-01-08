# compiler flags:
#  -g    adds debugging information to the executable file
#  -Wall turns on most, but not all, compiler warnings
CXXFLAGS  = -g -Wall -std=c++11 -fPIC
CPPFLAGS = -E

# the build target executable:
TARGET = miniweb
CPP_FILES := $(wildcard *.cc)
OBJ_FILES := $(addprefix obj/,$(CPP_FILES:.cc=.o))

LIBS = -lpthread -lboost_system-mt -lboost_filesystem-mt -lboost_program_options

all: init $(TARGET) $(TARGET).so

init:
	mkdir -p obj

$(TARGET): $(OBJ_FILES)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $^ $(LIBS) 

$(TARGET).so: $(OBJ_FILES)
	$(CXX) $(CXXFLAGS) -shared -o $(TARGET).so $^ $(LIBS)

obj/%.o: %.cc
	$(CXX) $(CXXFLAGS) -c -o $@ $<

clean:
	$(RM) $(TARGET)
	$(RM) $(TARGET).so
	$(RM) -rf obj/*
