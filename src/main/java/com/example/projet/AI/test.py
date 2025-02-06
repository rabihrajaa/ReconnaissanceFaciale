import sqlite3

def display_tables_content(db_path):
    try:
        # Connect to the SQLite database
        conn = sqlite3.connect(db_path)
        print("Database connected successfully")

        # Create a cursor object
        cursor = conn.cursor()

        # Fetch and display photos from the 'residents' table
        cursor.execute("SELECT photo FROM residents")
        residents_rows = cursor.fetchall()

        print("\nPhotos from the 'residents' table:")
        if residents_rows:
            for row in residents_rows:
                print(row[0])  # Accessing the photo path
        else:
            print("No photos found in the 'residents' table.")

        # Check if the 'foreigners' table exists
        cursor.execute("""
            SELECT name 
            FROM sqlite_master 
            WHERE type='table' AND name='foreigners';
        """)
        foreigners_table = cursor.fetchone()

        if foreigners_table:
            cursor.execute("SELECT photo FROM foreigners")
            foreigners_rows = cursor.fetchall()

            print("\nPhotos from the 'foreigners' table:")
            if foreigners_rows:
                for row in foreigners_rows:
                    print(row[0])  # Accessing the photo path
            else:
                print("No photos found in the 'foreigners' table.")
        else:
            print("\nThe 'foreigners' table does not exist.")

        # Check if the 'date_entre' table exists
        cursor.execute("""
            SELECT name 
            FROM sqlite_master 
            WHERE type='table' AND name='date_entre';
        """)
        date_entre_table = cursor.fetchone()

        if date_entre_table:
            cursor.execute("""
                SELECT id, resident_cin, id_foreigner, entry_date 
                FROM date_entre
            """)
            date_entre_rows = cursor.fetchall()

            print("\nEntries from the 'date_entre' table:")
            if date_entre_rows:
                for row in date_entre_rows:
                    entry_id, resident_cin, id_foreigner, entry_date = row
                    print(f"ID: {entry_id}, Resident CIN: {resident_cin}, Foreigner ID: {id_foreigner}, Entry Date: {entry_date}")
            else:
                print("No entries found in the 'date_entre' table.")
        else:
            print("\nThe 'date_entre' table does not exist.")

        # Close the connection
        conn.close()
    except sqlite3.Error as e:
        print(f"Error connecting to database: {e}")

# Path to your SQLite database
database_path = r"E:\demo\projet_db.sqlite"

if __name__ == "__main__":
    display_tables_content(database_path)
