#include <iostream>
#include <string>
#include <vector>
#include <algorithm>

using namespace std;

struct SPosition
{
    int x;
    int y;
};

int main()
{

    // game loop
    while (1) {
        int map_size; // The size of the map
        cin >> map_size; cin.ignore();
        SPosition apple;
        for (int i = 0; i < map_size; i++) {
            string row;
            getline(cin, row);
            if (row.find('A') != row.npos)
            {
                apple = {(int)row.find('A'), i};
            }
            cerr << row << endl;
        }
        string my_direction;
        cin >> my_direction; cin.ignore();
        string my_bonuses;
        getline(cin, my_bonuses);
        int my_size;
        cin >> my_size; cin.ignore();
        std::vector<SPosition> myBody;
        for (int i = 0; i < my_size; i++) {
            int x;
            int y;
            cin >> x >> y; cin.ignore();
            myBody.push_back({x,y});
        }
        string opponent_direction;
        cin >> opponent_direction; cin.ignore();
        string opponent_bonuses;
        getline(cin, opponent_bonuses);
        int opponent_size;
        cin >> opponent_size; cin.ignore();
        for (int i = 0; i < opponent_size; i++) {
            int body_x;
            int body_y;
            cin >> body_x >> body_y; cin.ignore();
        }
        int valid_action_count;
        cin >> valid_action_count; cin.ignore();
        for (int i = 0; i < valid_action_count; i++) {
            string valid_action;
            getline(cin, valid_action);
        }

        auto head = myBody.front();
        if (head.x != apple.x)
        {
            if (apple.x > head.x && my_direction != "LEFT")
            {
                cout << "RIGHT" << endl;
            }
            else
            {
                cout << "LEFT" << endl;
            }
        }
        else
        {
            if (apple.y > head.y && my_direction != "TOP")
            {
                cout << "DOWN" << endl;
            }
            else
            {
                cout << "UP" << endl;
            }
        }
    }
}