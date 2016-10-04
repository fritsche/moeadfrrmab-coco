#!/bin/bash

# ./run.sh command option1 option2 ... optionN

dependencies=".:lib/commons-math3-3.5/commons-math3-3.5.jar:lib/Jama-1.0.3.jar"
javac_options="-J-XX:MaxHeapSize=256m -J-Xmx512m"
java_options="-XX:MaxHeapSize=256m -Xmx512m"

function log () {
	echo -e "\e[1;34m $1 \e[0m"
}

function error () {
	echo -e "\e[1;31m ERROR: $1 \e[0m"	
}

# the search string is the first argument and the rest are the array elements
# contains_element element ${ARRAY} 
function contains_element () {
  local e
  for e in "${@:2}"; do [[ "$e" == "$1" ]] && return 0; done
  return 1
}

function checkfordependencies () {
	echo "checking for dependencies"
	if [ ! -d "lib" ];
	then
		echo "making lib directory"
		mkdir lib
	fi
	if [ ! -f "lib/WFG_1.10/wfg" ]; 
	then
		echo "downloading WFG hypervolume"
		cd lib
		wget "http://www.wfg.csse.uwa.edu.au/hypervolume/code/WFG_1.10.tar.gz"
		tar -zxvf "WFG_1.10.tar.gz"
		cd ..
	fi
	if [ ! -f "lib/commons-math3-3.5/commons-math3-3.5.jar" ];
	then
		echo "downloading commons match dependency"
		cd lib
		wget "http://mirror.cogentco.com/pub/apache//commons/math/binaries/commons-math3-3.5-bin.tar.gz"
		tar -zxvf "commons-math3-3.5-bin.tar.gz"
		cd ..
	fi
	if [ ! -f "lib/Jama-1.0.3.jar" ];
	then
		echo "downloading Jama dependency"
		cd lib
		wget "http://math.nist.gov/javanumerics/jama/Jama-1.0.3.jar"
		cd ..
	fi
}

function deploy () {
	checkfordependencies
	echo "fixing non ASCII chars on jmetal..."
	export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
	echo "loading java files..."
	find jmetal/ org/ test/ -name "*.java" > sources.aux
	echo "compiling..."
	javac -d ./build $javac_options -classpath $dependencies @sources.aux
	echo "create build folder"
	mkdir -p build
	echo "enter build folder"
	cd build
	pwd
	echo "create jar file"
	jar cvf moeadfrrmab.jar *
	echo "return"
	cd -
}

function compile () {
	checkfordependencies
	echo "fixing non ASCII chars on jmetal..."
	export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
	echo "loading java files..."
	find jmetal/ org/ test/ -name "*.java" > sources.aux
	echo "compiling..."
	javac $javac_options -classpath $dependencies @sources.aux
} # compile

function load_configuration () {

	log "loading experiment configuration..."
	if [ $# -eq 0 ]; then # if has no arguments
		. configurationFiles/experiment.conf
	else
		. $1
	fi

	IFS=',' read -ra mm                   <<< "$mm"
	IFS=',' read -ra iterations           <<< "$iterations"
	IFS=',' read -ra sizes                <<< "$sizes"
	IFS=',' read -ra problems             <<< "$problemList"
	IFS=',' read -ra algs 				  <<< "$algorithmsToRun"

}

function run_remote () {
	error "run_remote: Not implemented yet!"
}

function run_multi () {
	error "run_multi: Not implemented yet!"
}

function mab_configuration () {
	rm experiment.err
	rm experiment.out

	for file in $1/*.conf; do
		load_configuration $file
		echo $file
		for c in 10 5 1 0.5; do
			for w in 10 20 50; do
				for ((i=0; i<${#mm[@]}; ++i )); do
					echo "[$((i+1))/${#mm[@]}]"

					echo "c="$c  > configurationFiles/MABUCB.conf
					echo "w="$w >> configurationFiles/MABUCB.conf

					echo iterations=${iterations[i]}   > aux.conf
					echo size=${sizes[i]}             >> aux.conf
					echo m=${mm[i]}                   >> aux.conf
					echo runs=$runs                   >> aux.conf
					echo problems=$problemList        >> aux.conf
					echo run=$algorithmsToRun         >> aux.conf
					echo output=$outputName"C"$c"W"$w >> aux.conf
					echo experiment=$1                >> aux.conf

					java $java_options -classpath $dependencies jmetal.experiments.studies.MainStudy r aux.conf 1> experiment.out 2>> experiment.err

				done	
			done
		done

		rm aux.conf
	done
}

function run_local () {

	load_configuration $1

	log "running..."

	for ((i=0; i<${#mm[@]}; ++i )); do
	
		echo "[$((i+1))/${#mm[@]}]"
	
		echo iterations=${iterations[i]} > aux.conf
		echo size=${sizes[i]}           >> aux.conf
		echo m=${mm[i]}                 >> aux.conf
		echo runs=$runs                 >> aux.conf
		echo problems=$problemList      >> aux.conf
		echo run=$algorithmsToRun       >> aux.conf
		echo output=$outputName         >> aux.conf
		echo experiment=$2              >> aux.conf

		java $java_options -classpath $dependencies jmetal.experiments.studies.MainStudy r aux.conf 1> experiment.out 2>> experiment.err

	done	

	rm aux.conf
}

function run_set_queue () {
	rm experiment.err
	rm experiment.out

	j=0

	for file in $1/*.conf; do

		load_configuration $file

		for ((i=0; i<${#mm[@]}; ++i )); do
		
			echo "[$((i+1))/${#mm[@]}]"
		
			echo iterations=${iterations[i]} > aux$j.conf
			echo size=${sizes[i]}           >> aux$j.conf
			echo m=${mm[i]}                 >> aux$j.conf
			echo runs=$runs                 >> aux$j.conf
			echo problems=$problemList      >> aux$j.conf
			echo run=$algorithmsToRun       >> aux$j.conf
			echo output=$outputName         >> aux$j.conf
			echo experiment=$1              >> aux$j.conf

			qsub hhsmpsojob.sh "java $java_options -classpath $dependencies jmetal.experiments.studies.MainStudy r aux$j.conf"
		
			((j++))
		done

		echo $file
	done
}

function run_set () {

	rm experiment.err
	rm experiment.out

	for file in $1/*.conf; do
		run_local $file $1
		echo $file
	done
}

function evaluate () {
	
	load_configuration $1

	# rm experiment.err
	# rm experiment.out

	log "Generating quality indicators..."

	for ((i=0; i<${#mm[@]}; ++i )) ;do

		echo "[$((i+1))/${#mm[@]}]"

		for ((j=0; j<${#problems[@]}; ++j )) ;do

			echo m=${mm[i]}                           > aux.conf
			echo runs=$runs                           >> aux.conf
			echo problems=${problems[j]}              >> aux.conf
			echo run=$algorithmsToRun                 >> aux.conf
			echo evaluate=$algorithmsToEvaluate       >> aux.conf
			echo fronts="${problems[j]}_${mm[i]}.ref" >> aux.conf
			echo indicators=$indicatorList			  >> aux.conf
			echo experiment=$2                        >> aux.conf

			java $java_options -classpath $dependencies jmetal.experiments.studies.MainStudy i aux.conf 1>> experiment.out 2>> experiment.err

		done
	done

}

function gen_friedman () {
	# load configuration
	. $1

	log "Generating friedman test..."

	echo m=$mm		                          > aux.conf
	echo runs=$runs                           >> aux.conf
	echo problems=$problemList                >> aux.conf
	echo evaluate=$algorithmsToEvaluate       >> aux.conf
	echo indicators=$indicatorList			  >> aux.conf
	echo experiment=$2                        >> aux.conf

	java $java_options -classpath $dependencies jmetal.experiments.studies.MainStudy f aux.conf 1>> experiment.out 2>> experiment.err


}

function gen_latex () {

	load_configuration $1

	log "Generating latex files..."

	for ((i=0; i<${#mm[@]}; ++i )) ;do

			echo "[$((i+1))/${#mm[@]}]"

			echo m=${mm[i]}                           > aux.conf
			echo runs=$runs                           >> aux.conf
			echo problems=$problemList                >> aux.conf
			echo run=$algorithmsToRun                 >> aux.conf
			echo evaluate=$algorithmsToEvaluate       >> aux.conf
			echo fronts="${problems[j]}_${mm[i]}.ref" >> aux.conf
			echo indicators=$indicatorList			  >> aux.conf
			echo experiment=$2                        >> aux.conf

			java $java_options -classpath $dependencies jmetal.experiments.studies.MainStudy l aux.conf 1>> experiment.out 2>> experiment.err

	done

	rm aux.conf	
}

function print_info () {
	ps -eo cmd,etime | grep run.sh
	echo ""
	cat experiment.err | grep -v encoding=UTF8
	echo ""
	tail experiment.out
}

function run_watch () {
	while true; do clear; print_info; sleep 2; done 
}

function try_compile () {
	compile
	a=$?
	if ! [ $a -eq 0 ]; then
		error "COMPILATION ERROR!"
	else 
		log "COMPILATION SUCCESS!"
	fi
}

function generate_report () {

	load_configuration $1

	IFS=',' read -ra indicators <<< "$indicatorList" 
	IFS=',' read -ra algorithms <<< "$algorithmsToEvaluate"

	output=$2/$3

	echo "%$output" > $output
	echo "\documentclass[]{article}" >> $output
	echo "\usepackage{colortbl}" >> $output
	echo "\usepackage[table*]{xcolor}" >> $output
	echo "\usepackage{multirow}" >> $output
	echo "\usepackage{fixltx2e}" >> $output
	echo "\usepackage{stfloats}" >> $output
	echo "\usepackage{psfrag}" >> $output
	echo "\usepackage[]{threeparttable}" >> $output
	echo "\usepackage{multicol}" >> $output
	echo "\usepackage{lscape}" >> $output
	echo "\xdefinecolor{gray95}{gray}{0.75}" >> $output
	echo "\begin{document}" >> $output

	if (( ${#algorithms[@]} > 4 )); then
		echo "\begin{landscape}" >> $output
	fi

	for indicator in "${indicators[@]}"; do
		echo "\begin{table}" >> $output
		echo "\caption{$indicator. Mean and standard deviation}" >> $output
		echo "\label{table:mean.$indicator}" >> $output
		echo "\centering" >> $output
		echo "\begin{footnotesize}" >> $output
		aux=""
		head=""
		for (( i = 0; i < ${#algorithms[@]}; i++ )); do
			aux=$aux"l|"
			head=$head" & "${algorithms[i]}
		done
		echo "\begin{tabular}{|l|l|$aux}" >> $output
		echo "\hline" >> $output
		echo "Obj. & problem $head \\\\ \hline" >> $output
		for (( i = 0; i < ${#mm[@]}; i++ )); do
			cat "$2/${mm[i]}/latex/$indicator.tex" >> $output 
			echo "\hline" >> $output
		done
		echo "\end{tabular}" >> $output
		echo "\end{footnotesize}" >> $output
		echo "\end{table}" >> $output

	done

	if (( ${#algorithms[@]} > 4 )); then
		echo "\end{landscape}" >> $output
	fi

	echo "\end{document}" >> $output
	
	# pdflatex $output

}

function backup () {
	log "creating tunnel..."
	ssh -fN -l gmfritsche -L 2222:hydra:22 ssh.c3sl.ufpr.br &

	# log "backup from orval ~/cuda/ to samsung ~/Documents/cuda/"
	# rsync -auzh --info=progress2,stats1 gmfritsche@ssh.c3sl.ufpr.br:cuda/ ~/Documents/cuda/

	# edit ( ~/.ssh/config ) to looks like:
	# Host hydra
	# HostName localhost
	# Port 2222
	# HostKeyAlias hydra
	# User gian

	log "backup from samsung ~/hhsmpso to hydra ~/samsung/"
	rsync -auzh --info=progress2,stats1 ~/hhsmpso/ hydra:~/samsung/

	log "backup from hydra ~/hhsmpso to samsung ~/Documents/hydra/"
	rsync -auzh --info=progress2,stats1 hydra:~/hhsmpso/ ~/Documents/hydra/
}

# begin main
clear

if [ "$1" == "local" ]; then
	rm experiment.err
	rm experiment.out
	try_compile
	time run_local $2 $3
elif [ "$1" == "eval" ]; then # evaluates quality indicator and generate latex files
	try_compile
	time evaluate $2 $3
	time gen_latex $2 $3
elif [ "$1" == "indicator" ]; then # evaluates quality indicator
	try_compile
	time evaluate $2 $3
elif [ "$1" == "latex" ]; then # generate latex files
	try_compile
	time gen_latex $2 $3
elif [ "$1" == "friedman" ]; then # generate friedman test
	try_compile
	time gen_friedman $2 $3
elif [ "$1" == "-co" ]; then
	try_compile
elif [ "$1" == "deploy" ]; then
	deploy
elif [ "$1" == "watch" ]; then
	time run_watch
elif [ "$1" == "set" ]; then
	# try_compile
	time run_set $2
elif [ "$1" == "queue" ]; then
	try_compile
	time run_set_queue $2
elif [ "$1" == "mab" ]; then
	try_compile
	time mab_configuration $2
elif [ "$1" == "report" ]; then
	time generate_report $2 $3 $4
elif [ "$1" == "backup" ]; then
	time backup
else
	error "unknown command [$1]"
fi

echo "removing aux files..."
rm *.aux
echo "done."
