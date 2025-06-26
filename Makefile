JC := javac
JFLAGS := -Xlint:all
OBJDIR := bin
STOREDIR := expenseTracker/expenseStorage
MAINDIR := expenseTracker
PROGNAME := expenseTracker.jar

all: jar

jar: ExpenseUI.class
	cd ${OBJDIR}; jar -cfe ../expenseTracker.jar ${MAINDIR}.ExpenseUI ./${MAINDIR}/ExpenseUI.class ./${STOREDIR}/*

ExpenseUI.class: IExpenseStorage.class ArrayExpenseStorage.class ExpenseRecord.class
	${JC} ${JFLAGS} --class-path . -d ./${OBJDIR}/ ./${MAINDIR}/ExpenseUI.java

IExpenseStorage.class:
	${JC} ${JFLAGS} --class-path . -d ./${OBJDIR}/ ./${STOREDIR}/IExpenseStorage.java

ArrayExpenseStorage.class:
	${JC} ${JFLAGS} --class-path . -d ./${OBJDIR}/ ./${STOREDIR}/ArrayExpenseStorage.java

ExpenseRecord.class:
	${JC} ${JFLAGS} --class-path . -d ./${OBJDIR}/ ./${STOREDIR}/ExpenseRecord.java

clean:
	rm -rf ${OBJDIR}
	rm -f ${PROGNAME}