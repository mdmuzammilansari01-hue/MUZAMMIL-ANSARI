-- =============================================================================
-- JAC TEST HUB - COMPLETE SUPABASE DATABASE SCHEMA & INITIALIZATION SCRIPT
-- Project Name: JAC Test Hub
-- Database: PostgreSQL (Supabase)
-- =============================================================================

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- -----------------------------------------------------------------------------
-- 1. ADMINS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.admins (
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4()::text,
    email TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    role TEXT DEFAULT 'Admin',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 2. STUDENTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.students (
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4()::text,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    class_name TEXT NOT NULL,
    roll_no TEXT,
    is_blocked BOOLEAN DEFAULT FALSE,
    joined_date TEXT DEFAULT 'July 2026',
    tests_taken INT DEFAULT 0,
    avg_score TEXT DEFAULT '0%',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 3. CLASSES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.classes (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    code TEXT NOT NULL,
    stream TEXT,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 4. SUBJECTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.subjects (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    class_name TEXT NOT NULL,
    chapters_count INT DEFAULT 0,
    icon_name TEXT DEFAULT 'Book',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 5. CHAPTERS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.chapters (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    class_name TEXT NOT NULL,
    subject TEXT NOT NULL,
    summary TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 6. NOTES TABLE (PDF STUDY MATERIAL)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.notes (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    class_name TEXT NOT NULL,
    subject TEXT NOT NULL,
    category TEXT DEFAULT 'Formula Sheet',
    pdf_url TEXT NOT NULL,
    pages_count INT DEFAULT 12,
    file_size_mb TEXT DEFAULT '2.4 MB',
    content_summary TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 7. MCQS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.mcqs (
    id TEXT PRIMARY KEY,
    question_text TEXT NOT NULL,
    options JSONB NOT NULL,
    correct_index INT NOT NULL,
    explanation TEXT,
    subject TEXT NOT NULL,
    class_name TEXT NOT NULL,
    difficulty TEXT DEFAULT 'Medium',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 8. MOCK TESTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.mock_tests (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    class_name TEXT NOT NULL,
    subject TEXT NOT NULL,
    duration_minutes INT DEFAULT 60,
    total_marks INT DEFAULT 100,
    is_published BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 9. VIDEOS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.videos (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    class_name TEXT NOT NULL,
    subject TEXT NOT NULL,
    educator_name TEXT,
    duration TEXT DEFAULT '25 mins',
    video_url TEXT NOT NULL,
    thumbnail_color_hex TEXT DEFAULT '#004D40',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 10. NOTIFICATIONS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.notifications (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    target_class TEXT DEFAULT 'All Students',
    priority TEXT DEFAULT 'Normal',
    sent_timestamp TEXT DEFAULT 'Just now',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 11. RESULTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.results (
    id BIGINT PRIMARY KEY,
    test_id TEXT NOT NULL,
    test_title TEXT NOT NULL,
    class_name TEXT NOT NULL,
    subject TEXT NOT NULL,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    accuracy_percentage INT NOT NULL,
    time_taken_seconds INT NOT NULL,
    student_email TEXT,
    student_name TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- =============================================================================
-- STORAGE BUCKET CONFIGURATION FOR PDF NOTES
-- =============================================================================
INSERT INTO storage.buckets (id, name, public)
VALUES ('pdf-notes', 'pdf-notes', true)
ON CONFLICT (id) DO NOTHING;

-- Storage Security Policies
CREATE POLICY "Public Read Access for PDF Notes"
ON storage.objects FOR SELECT
USING (bucket_id = 'pdf-notes');

CREATE POLICY "Allow Upload to PDF Notes"
ON storage.objects FOR INSERT
WITH CHECK (bucket_id = 'pdf-notes');

CREATE POLICY "Allow Delete from PDF Notes"
ON storage.objects FOR DELETE
USING (bucket_id = 'pdf-notes');

-- =============================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES FOR ALL TABLES
-- =============================================================================
ALTER TABLE public.admins ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.students ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.classes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.subjects ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.chapters ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.mcqs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.mock_tests ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.videos ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.results ENABLE ROW LEVEL SECURITY;

-- Allow Public / Anon & Authenticated full access for client integration
CREATE POLICY "Allow anon select admins" ON public.admins FOR SELECT USING (true);
CREATE POLICY "Allow anon insert admins" ON public.admins FOR INSERT WITH CHECK (true);

CREATE POLICY "Allow anon select students" ON public.students FOR SELECT USING (true);
CREATE POLICY "Allow anon insert students" ON public.students FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow anon update students" ON public.students FOR UPDATE USING (true);

CREATE POLICY "Allow anon all classes" ON public.classes FOR ALL USING (true);
CREATE POLICY "Allow anon all subjects" ON public.subjects FOR ALL USING (true);
CREATE POLICY "Allow anon all chapters" ON public.chapters FOR ALL USING (true);
CREATE POLICY "Allow anon all notes" ON public.notes FOR ALL USING (true);
CREATE POLICY "Allow anon all mcqs" ON public.mcqs FOR ALL USING (true);
CREATE POLICY "Allow anon all mock_tests" ON public.mock_tests FOR ALL USING (true);
CREATE POLICY "Allow anon all videos" ON public.videos FOR ALL USING (true);
CREATE POLICY "Allow anon all notifications" ON public.notifications FOR ALL USING (true);
CREATE POLICY "Allow anon all results" ON public.results FOR ALL USING (true);

-- =============================================================================
-- SAMPLE DATA SEEDING
-- =============================================================================
INSERT INTO public.classes (id, name, code, stream, description) VALUES
('c10', 'Class 10', 'JAC-10', 'General Secondary', 'Jharkhand Academic Council Class 10 Board Preparation'),
('c12_sci', 'Class 12 Science', 'JAC-12-S', 'Science Stream', 'Physics, Chemistry, Math & Biology for JAC Class 12')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.admins (id, email, full_name, role) VALUES
('admin_1', 'ansarimuzammil0018@gmail.com', 'Md. Muzammil Ansari', 'Super Admin')
ON CONFLICT (id) DO NOTHING;
