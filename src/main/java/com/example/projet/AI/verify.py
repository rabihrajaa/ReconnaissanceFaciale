import sys
import sqlite3
from deepface import DeepFace
from datetime import datetime

def get_authorized_residents_images(database_path):
    try:
        conn = sqlite3.connect(database_path)
        cursor = conn.cursor()

        # Query the database to get image URLs and CIN for authorized residents
        cursor.execute("SELECT photo, CIN FROM residents WHERE status = 'Authorized'")
        images = cursor.fetchall()

        conn.close()
        return images
    except sqlite3.Error as e:
        print(f"Database error while fetching authorized images: {e}")
        return []

def save_foreigner_entry(database_path, image_path):
    try:
        conn = sqlite3.connect(database_path)
        cursor = conn.cursor()

        # Insert the foreigner's image into the foreigners table
        cursor.execute("INSERT INTO foreigners (photo) VALUES (?)", (image_path,))
        foreigner_id = cursor.lastrowid

        # Insert entry date and time into the date_entre table for the foreigner
        cursor.execute("INSERT INTO date_entre (id_foreigner) VALUES (?)", (foreigner_id,))
        conn.commit()

        print(f"Saved unmatched image: {image_path} with foreigner ID {foreigner_id}.")
        conn.close()
    except sqlite3.Error as e:
        print(f"Database error while saving foreigner entry: {e}")

def save_resident_entry(database_path, resident_cin):
    try:
        conn = sqlite3.connect(database_path)
        cursor = conn.cursor()

        # Insert entry date and time into the date_entre table for the resident
        cursor.execute("INSERT INTO date_entre (resident_cin) VALUES (?)", (resident_cin,))
        conn.commit()

        print(f"Saved entry for resident CIN: {resident_cin}.")
        conn.close()
    except sqlite3.Error as e:
        print(f"Database error while saving resident entry: {e}")

def compare_face(image_path, authorized_images, database_path):
    try:
        for db_image, cin in authorized_images:
            print(f"Comparing captured image {image_path} with authorized database image {db_image}")

            # Perform face verification using DeepFace
            result = DeepFace.verify(image_path, db_image, enforce_detection=False)
            print(f"Comparison result for {db_image}: {result}")

            if result["verified"]:
                print(f"Access authorized for resident CIN: {cin}")
                save_resident_entry(database_path, cin)
                return "Access Authorized"

        # If no match found, save the unmatched image as a foreigner
        print("No authorized match found. Saving image to foreigners table.")
        save_foreigner_entry(database_path, image_path)
        return "Access Not Authorized"
    except Exception as e:
        print(f"Error in face comparison: {e}")
        return f"Error in face comparison: {e}"

def main():
    if len(sys.argv) < 2:
        print("Error: No image path provided.")
        return

    captured_image_path = sys.argv[1]
    database_path = r"E:\demo\projet_db.sqlite"

    authorized_images = get_authorized_residents_images(database_path)
    if not authorized_images:
        print("No authorized images found in the database.")
        return

    result = compare_face(captured_image_path, authorized_images, database_path)
    print(result)

if __name__ == "__main__":
    main()
