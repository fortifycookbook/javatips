/*
 * Copyright 2016 Micro Focus or one of its affiliates.
 */
#include <iostream>
#include <fstream>
#include <vector>
#include <string>

using namespace std;

string get_env_as_string(const char*);
bool is_valid_filepath(const string&, const string&);

int main(int argc, const char * argv[]) {
    if (argc <= 1) {
        return EXIT_FAILURE;
    }
    
    vector<string> args;
    bool has_build_action = false;
    
    for (int i = 0; i < argc; ++i) {
        if (strncmp(argv[i], "build", strlen("build")) == 0) {
            has_build_action = true;
        }
        args.push_back(argv[i]);
    }
    
    // Validate input
    const string sourceanalyzer_path = get_env_as_string("FORTIFY_SCA_EXECUTABLE_PATH");
    if (!is_valid_filepath(sourceanalyzer_path, "sourceanalyzer")) {
        cout << "ERROR: Couldn't find sourceanalzer." << endl << flush;
        return EXIT_FAILURE;
    }

    const string sca_options = get_env_as_string("FORTIFY_SCA_OPTIONS");
    if (sca_options.empty()) {
        cout << "ERROR: Couldn't find valid options for sourceanalyzer." << endl << flush;
        return EXIT_FAILURE;
    }

    const string user_home = get_env_as_string("HOME");
    if (user_home.empty()) {
        cout << "ERROR: Couldn't find user's home directory." << endl << flush;
        return EXIT_FAILURE;
    }
    
    const string org_xcodebuild_path = get_env_as_string("ORIGINAL_XCODEBUILD_PATH");
    if (!is_valid_filepath(org_xcodebuild_path, "xcodebuild")) {
        cout << "ERROR: Couldn't find xcodebuild." << endl << flush;
        return EXIT_FAILURE;
    }
    
    // Run command
    vector<string> commands;
    if (has_build_action) {
        commands.push_back(sourceanalyzer_path);
        commands.push_back(sca_options);
        commands.insert(commands.end(), args.begin(), args.end());
        
        const string xcodebuild_path = user_home + "/.fortify/maven/xcodebuild";
        
        for (vector<string>::iterator iter = commands.begin(); iter != commands.end(); ++iter) {
            if (*iter == xcodebuild_path || *iter == "xcodebuild") {
                *iter = org_xcodebuild_path;
                break;
            }
        }
    } else {
        commands.push_back(org_xcodebuild_path);
        vector<string>::iterator iter = args.begin();
        args.erase(iter);
        commands.insert(commands.end(), args.begin(), args.end());
    }
    
    string command;
    for (vector<string>::const_iterator iter = commands.begin(); iter != commands.end(); ++iter) {
        command += *iter + " ";
    }
    
    cout << "Executing command: " << command.c_str() << endl << flush;
    
    return system(command.c_str());
}

string get_env_as_string(const char* key) {
    const char* value = getenv(key);

    if (value == 0) {
        return "";
    } else {
        return value;
    }
}

bool is_valid_filepath(const string& filepath, const string& filename) {
    if (filepath.empty() || filename.empty()) return false;
    if (filepath.find("..") != string::npos) return false;
    if (filepath.find("&&") != string::npos) return false;
    if (filepath.find("|") != string::npos) return false;
    if (filepath.find(";") != string::npos) return false;
    if (filepath.size() < filename.size() || filepath.compare(filepath.size() - filename.size(), filename.size(), filename) != 0) return false;
    
    return true;
}
